package commands;

import exceptions.WrongAuthRedirectUriException;
import logic.ActiveUsers;
import org.apache.commons.logging.impl.AvalonLogger;
import org.telegram.telegrambots.meta.api.objects.User;
import spotify.exceptions.SpotifyActionFailedException;
import spotify.exceptions.SpotifyAuthorizationFailedException;
import spotify.models.errors.SpotifyError;
import spotify_tools.SpotifySession;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class NonCommand {
    /**
     * @param authRedirectUri сообщение, вероятно содержащее аутентификационный код
     * @throws WrongAuthRedirectUriException строка не соответствует шаблону аутентификационного сообщения
     */
    public static String getCode(String authRedirectUri) throws WrongAuthRedirectUriException {

        if (authRedirectUri == null) {
            throw new WrongAuthRedirectUriException();
        }

        String uriBase;

        final String defaultUriBase = "http://localhost:8080/auth/spotify/redirect";
        final String evUriBase = System.getenv("SPOTIFY_REDIRECT_URI");

        if (evUriBase == null || evUriBase.length() == 0) {
            uriBase = defaultUriBase;
        }

        else {
            uriBase = evUriBase;
        }

        final Pattern pattern = Pattern.compile(uriBase + "\\?code=([-\\w]+)|[-\\w]+=[-\\w]+&code=([-\\w]+)");
        final Matcher matcher = pattern.matcher(authRedirectUri);

        if (matcher.find()) {
            if (matcher.group(1) == null) {
                return matcher.group(2);
            }

            return matcher.group(1);
        }

        throw new WrongAuthRedirectUriException();
    }

    public String nonCommandExecute(User user, String userName, String text) {
        String answer;
        String code;
        try {
            code = getCode(text);
            SpotifySession session = ActiveUsers.getSession(user);
            session.setCode(code);
            session.buildAuthorizationRequestToken();
            session.buildSpotifyApi();

            ActiveUsers.updateActiveUsers(user, session);
            answer = session.spotifyApi.getCurrentUser().getDisplayName() + ", вы успешно авторизовались!";
        } catch (WrongAuthRedirectUriException e) {
            answer = "Похоже вы неправильно ввели ссылку, попробуйте ещё раз";
        } catch (SpotifyAuthorizationFailedException | SpotifyActionFailedException e) {
            answer = "Неверный код. Попробуйте ещё раз.";
        } catch (Exception e) {
            answer = "Простите, я не понимаю Вас. Похоже, что Вы ввели сообщение, не соответствующее формату. Возможно, Вам поможет /help";
        }
        return answer;
    }
}

