package commands;

import logic.ActiveGroups;
import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import spotify.api.enums.QueryType;
import spotify.exceptions.SpotifyActionFailedException;
import spotify.models.errors.SpotifyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Обработка сообщений с командой добавления трека в очередь прослушиваемых.
 */
public class AddCommand extends ServiceCommand {

    public AddCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());

        if (ActiveGroups.getGroupSession(chat) == null) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                    new StringBuilder()
                            .append("Групповая музыкальная сессия в этом чате не создана.")
                            .append("\n\n")
                            .append("Запустите групповую сессию при помощи команды /group").toString());
            return;
        }

        User leader = ActiveGroups.getGroupSession(chat).getLeader();

        if (ActiveUsers.getSession(leader) == null) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                    new StringBuilder()
                            .append("Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.")
                            .append("\n\n")
                            .append("Для этого введите в чат со мной команду /auth").toString());
            return;
        }

        if (ActiveUsers.getSession(leader).getTokenExpiresIn() < 30) {
            ActiveUsers.getSession(leader).authorizeByRefreshToken();
        }

        Map<String, String> properties = Map.of(
                "market", "TR",
                "limit", "1"
        );

        List<QueryType> types = List.of(QueryType.TRACK);

        StringBuilder search = new StringBuilder();

        for (String commandArgument : strings) {
            search.append(commandArgument);
            search.append(" ");
        }

        var tracks = ActiveUsers.getSession(leader)
                .getSpotifyApi()
                .searchItem(search.toString(), types, properties)
                .getTracks()
                .getItems();

        var foundTrack = tracks.get(0);

        try {
            ActiveUsers.getSession(leader).getSpotifyApi().addItemToQueue(foundTrack.getUri(), null);
        } catch (SpotifyActionFailedException e) {
            ActiveGroups.closeGroupSession(chat);
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                    new StringBuilder()
                            .append("Похоже, у лидера отсутствует подписка Spotify Premium. Групповая сессия закрыта.")
                            .append("\n\n")
                            .append("Попробуйте создать группу с другим лидером, у которого оплачена подписка.").toString());
            return;
        }

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                String.format("[%s — %s](%s) добавлена в очередь",
                        foundTrack.getName(),
                        foundTrack.getArtists().get(0).getName(),
                        foundTrack.getExternalUrls().getSpotify()));
    }
}