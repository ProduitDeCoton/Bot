package commands;

import exceptions.WrongAuthRedirectUriException;
import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.User;
import spotify.exceptions.SpotifyActionFailedException;
import spotify.exceptions.SpotifyAuthorizationFailedException;
import spotifyTools.SpotifySession;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для обработки сообщений, вероятно содержащих код
 * для получения токенов.
 */
public class NonCommand {

    /**
     * @param authRedirectUri сообщение, вероятно содержащее аутентификационный код
     * @throws WrongAuthRedirectUriException строка не соответствует шаблону аутентификационного сообщения
     */
    public static String getCode(String authRedirectUri) throws WrongAuthRedirectUriException {

        if (authRedirectUri == null) {
            throw new WrongAuthRedirectUriException();
        }

        final String uriBase = "http://localhost:8080/auth/spotify/redirect";
        final Pattern pattern = Pattern.compile(uriBase + "\\?code=([-_A-Za-z0-9]+)");
        final Matcher matcher = pattern.matcher(authRedirectUri);

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new WrongAuthRedirectUriException();
    }

    public String nonCommandExecute(User user, String text) {
        String answer;

        if (text == null) {
            answer = "Простите, я не понимаю Вас. Похоже, что Вы ввели сообщение, не соответствующее формату. Возможно, Вам поможет /help";
            return answer;
        }

        try {
            String code = getCode(text);
            SpotifySession session = ActiveUsers.getSession(user);
            session.authorizeByCode(code);

            ActiveUsers.updateActiveUsers(user, session);
            answer = session.getSpotifyApi().getCurrentUser().getDisplayName() + ", вы успешно авторизовались!";
        } catch (WrongAuthRedirectUriException e) {
            answer = "Похоже, вы неправильно ввели ссылку, попробуйте ещё раз";
        } catch (SpotifyAuthorizationFailedException | SpotifyActionFailedException e) {
            answer = "Неверный код. Попробуйте ещё раз.";
        }
        return answer;
    }
}

