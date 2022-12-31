package commands;

import logic.ActiveGroups;
import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import resources.CommonAnswers;
import spotify.api.enums.QueryType;
import spotify.exceptions.SpotifyActionFailedException;
import java.util.List;
import java.util.Map;

public class AddCommand extends ServiceCommand {

    public AddCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        if (ActiveGroups.getGroupSession(chat) == null) {
            sendAnswer(absSender, chat.getId(), CommonAnswers.GROUP_NOT_CREATED);
            return;
        }

        User leader = ActiveGroups.getGroupSession(chat).getLeader();

        if (ActiveUsers.getSession(leader) == null) {
            sendAnswer(absSender, chat.getId(), CommonAnswers.USER_NOT_AUTHORISED);
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
            sendAnswer(absSender, chat.getId(), CommonAnswers.GROUP_LEADER_PREMIUM_EXPIRED);
            return;
        }

        sendAnswer(absSender, chat.getId(),
                "[" + foundTrack.getName() + " — " + foundTrack.getArtists().get(0).getName()
                    + "](" + foundTrack.getExternalUrls().getSpotify() + ") добавлена в очередь."
        );
    }
}
