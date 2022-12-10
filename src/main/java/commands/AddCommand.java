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
    public void execute(AbsSender absSender, User user, Chat chat, String[] args) {

        if (ActiveGroups.getGroupSession(chat) == null) {
            sendAnswer(absSender, chat.getId(),
                    """
                            Групповая музыкальная сессия в этом чате не создана.

                            Запустите групповую сессию при помощи команды /group""");
            return;
        }

        final User leader = ActiveGroups.getGroupSession(chat).getLeader();

        if (ActiveUsers.getSession(leader) == null) {
            sendAnswer(absSender, chat.getId(),
                    """
                            Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.

                            Для этого введите в чат со мной команду /auth""");
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
            sendAnswer(absSender, chat.getId(),
                    """
                            Похоже, у лидера отсутствует подписка Spotify Premium. Групповая сессия закрыта.

                            Попробуйте создать группу с другим лидером, у которого оплачена подписка.""");
            return;
        }

        sendAnswer(absSender, chat.getId(),
                "[" + foundTrack.getName() + " — " + foundTrack.getArtists().get(0).getName()
                    + "](" + foundTrack.getExternalUrls().getSpotify() + ") добавлена в очередь."
        );
    }
}