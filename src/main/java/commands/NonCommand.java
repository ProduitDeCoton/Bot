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
 * Класс обработки сообщений с Auth Redirect URI.
 */
public class NonCommand {

    private static final String _authUriBase = "http://localhost:8080/auth/spotify/redirect";
    private static final Pattern _authCodeMessageаPattern = Pattern.compile(_authUriBase + "\\?code=([-_A-Za-z0-9]+)");

    /**
     * Вычленить код аутентификации из текста сообщения с Auth Redirect URI.
     *
     * @throws WrongAuthRedirectUriException строка не содержит корректный Auth Redirect URI.
     */
    public static String getCode(final String authRedirectUri) throws WrongAuthRedirectUriException {

        if (authRedirectUri == null) {
            throw new WrongAuthRedirectUriException();
        }

        final Matcher matcher = _authCodeMessageаPattern.matcher(authRedirectUri);

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new WrongAuthRedirectUriException();
    }

    public String nonCommandExecute(User user, String text) {
        String answer;

        if (text == null) {
            return "Простите, я не понимаю Вас. Похоже, что Вы ввели сообщение, не соответствующее формату. Возможно, Вам поможет /help";
        }

        try {
            final String code = getCode(text);
            final SpotifySession session = ActiveUsers.getSession(user);
            session.authorizeByCode(code);

            ActiveUsers.updateActiveUsers(user, session);
            answer = String.format("%s, вы успешно авторизовались.", session.getSpotifyApi().getCurrentUser().getDisplayName());

        } catch (WrongAuthRedirectUriException e) {
            answer = "Похоже, вы неправильно ввели ссылку, попробуйте ещё раз.";

        } catch (SpotifyAuthorizationFailedException | SpotifyActionFailedException e) {
            answer = "Неверный код. Попробуйте ещё раз.";
        }

        return answer;
    }
}

