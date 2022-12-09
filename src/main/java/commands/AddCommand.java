package commands;

import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import spotify.api.enums.QueryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCommand extends ServiceCommand {

    public AddCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings){
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());

        if (ActiveUsers.getSession(user) == null) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                    userName + ", пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной." +
                            "\n\n" + "Для этого введите в чат со мной команду /auth");
            return;
        }


        Map<String, String> properties = new HashMap<>();
        properties.put("market", "TR");
        List<QueryType> types = new ArrayList<>();
        types.add(QueryType.TRACK);
        
        String qs = new String("");
        var tracks = ActiveUsers.getSession(user).getSpotifyApi().searchItem(qs,types,properties);

        tracks.getTracks();
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, tracks.toString());
    }
}
