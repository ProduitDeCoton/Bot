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

    /**
     * Зарегистрировать команду авторизации.
     *
     * @param identifier  уникальное название команды.
     * @param description описание команды.
     */
    public AuthCommand(final String identifier, final String description) {
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
     * Обработка команды авторизации.
     */
    @Override
    public void execute(final AbsSender absSender, final User user, final Chat chat, final String[] args) {
        final String userAppeal = getUserAppeal(user);

        if (!chat.getType().equals("private")) {
            sendAnswer(absSender, chat.getId(),
                    """
                            Похоже, вы пытаетесь авторизоваться в публичном чате.

                            Пожалуйста, выполняйте авторизацию в личном чате со мной.""");
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

        sendAnswer(absSender, chat.getId(),
                markDownCompatibleUserAppeal + ", пожалуйста, пройдите по ссылке ниже. Пройдите аутентификацию и предоставьте " +
                        "разрешения для работы бота." + "\n" + "\n" + "[Пройти авторизацию]" + "(" + redirectLink + ")" + "\n" + "\n" +
                        "После аутентификации в адресной строке появится ссылка с кодом. Отправьте, пожалуйста, " +
                        "всю ссылку целиком.");
    }
}
