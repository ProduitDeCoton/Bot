package commands;

import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import spotify_tools.SpotifySession;

public class AuthCommand extends ServiceCommand {
    public AuthCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        var session = ActiveUsers.getSession(user);

        if (session == null) {
            SpotifySession spotifySession = new SpotifySession();
            ActiveUsers.updateActiveUsers(user, spotifySession);

            session = ActiveUsers.getSession(user);
        }

        String redirectLink = session.buildAuthorizationCodeFlow().replace("_", "\\_");

        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());

        userName = userName.replace("_", "\\_");
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                userName + ", пожалуйста, пройдите по ссылке ниже. Пройдите аутентификацию и предоставьте " +
                        "разрешения для работы бота." + "\n" + "\n" + redirectLink + "\n" + "\n" +
                        "После аутентификации в адресной строке появится ссылка с кодом. Отправьте, пожалуйста, " +
                        "всю ссылку целиком.");
    }
}
