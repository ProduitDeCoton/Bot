package commands;

import logic.ActiveGroups;
import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import spotify.api.enums.QueryType;
import spotify.exceptions.SpotifyActionFailedException;

import java.util.List;
import java.util.Map;

/**
 * Обработка сообщений с командой добавления трека в очередь прослушиваемых.
 */
public class AddCommand extends ServiceCommand {

    /**
     * Зарегистрировать команду добавления трека в очередь.
     *
     * @param identifier  уникальное название команды.
     * @param description описание команды.
     */
    public AddCommand(final String identifier, final String description) {
        super(identifier, description);
    }

    /**
     * Сформировать обращение к пользователю.
     * Никнейм первичен. Если ник не установлен, обращаемся по имени и фамилии.
     */
    private String getUserAppeal(final User user) {
        final String appeal = user.getUserName();

        if (appeal == null) {
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        }

        return appeal;
    }

    /**
     * Обработка команды добавления трека в очередь.
     */
    @Override
    public void execute(final AbsSender absSender, final User user, final Chat chat, final String[] args) {
        final String userAppeal = getUserAppeal(user);

        if (ActiveGroups.getGroupSession(chat) == null) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userAppeal,
                    new StringBuilder()
                            .append("Групповая музыкальная сессия в этом чате не создана.")
                            .append("\n\n")
                            .append("Запустите групповую сессию при помощи команды /group").toString());
            return;
        }

        final User leader = ActiveGroups.getGroupSession(chat).getLeader();

        if (ActiveUsers.getSession(leader) == null) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userAppeal,
                    new StringBuilder()
                            .append("Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.")
                            .append("\n\n")
                            .append("Для этого введите в чат со мной команду /auth").toString());
            return;
        }

        if (ActiveUsers.getSession(leader).getTokenExpiresIn() <= 30) {
            ActiveUsers.getSession(leader).authorizeByRefreshToken();
        }

        final Map<String, String> properties = Map.of(
                "market", "TR",
                "limit", "1"
        );

        final List<QueryType> types = List.of(QueryType.TRACK);
        final StringBuilder search = new StringBuilder();

        for (final String arg : args) {
            search.append(arg);
            search.append(" ");
        }

        final var tracks = ActiveUsers.getSession(leader)
                .getSpotifyApi()
                .searchItem(search.toString(), types, properties)
                .getTracks()
                .getItems();

        final var foundTrack = tracks.get(0);

        try {
            ActiveUsers.getSession(leader).getSpotifyApi().addItemToQueue(foundTrack.getUri(), null);

        } catch (SpotifyActionFailedException e) {
            ActiveGroups.closeGroupSession(chat);
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userAppeal,
                    new StringBuilder()
                            .append("Похоже, у лидера отсутствует подписка Spotify Premium. ")
                            .append("Групповая сессия закрыта.")
                            .append("\n\n")
                            .append("Попробуйте создать группу с другим лидером, у которого оплачена подписка.").toString());
            return;
        }

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userAppeal,
                String.format("[%s — %s](%s) добавлена в очередь",
                        foundTrack.getName(),
                        foundTrack.getArtists().get(0).getName(),
                        foundTrack.getExternalUrls().getSpotify()));
    }
}