package commands;

import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import spotifyTools.SpotifySession;

/**
 * Команда для начала авторизации. Генерирует редирект-ссылку и возвращает
 * её пользователю.
 */
public class AuthCommand extends ServiceCommand {
    public AuthCommand(final String identifier, final String description) {
        super(identifier, description);
    }

    /**
     * Сформировать обращение к пользователю.
     *
     * @param user телеграмовский пользователь
     */
    private String getUserAppeal(final User user) {
        final String appeal = user.getUserName();

        if (appeal == null) {
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        }

        return appeal;
    }

    @Override
    public void execute(final AbsSender absSender, final User user, final Chat chat, final String[] args) {
        final String userAppeal = getUserAppeal(user);

        if (!chat.getType().equals("private")) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userAppeal,
                    new StringBuilder()
                            .append("Похоже, вы пытаетесь авторизоваться в публичном чате.")
                            .append("\n\n")
                            .append("Пожалуйста, выполняйте авторизацию в личном чате со мной.").toString());
            return;
        }

        var session = ActiveUsers.getSession(user);

        if (session == null) {
            SpotifySession spotifySession = new SpotifySession();
            ActiveUsers.updateActiveUsers(user, spotifySession);

            session = ActiveUsers.getSession(user);
        }

        final String redirectLink = session.buildAuthorizationCodeFlow();
        final String markDownCompatibleUserAppeal = userAppeal.replace("_", "\\_");

        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userAppeal,
                new StringBuilder()
                        .append(String.format("%s, пожалуйста, пройдите по ссылке ниже. ", markDownCompatibleUserAppeal))
                        .append("Пройдите аутентификацию и предоставьте разрешения для работы бота.")
                        .append("\n\n")
                        .append(String.format("[Пройти авторизацию](%s)", redirectLink))
                        .append("\n\n")
                        .append("После аутентификации в адресной строке появится ссылка с кодом. ")
                        .append("Отправьте, пожалуйста, всю ссылку целиком").toString());
    }
}
