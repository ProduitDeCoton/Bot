package spotify_tools;

import spotify.api.authorization.AuthorizationCodeFlow;
import spotify.api.authorization.AuthorizationRefreshToken;
import spotify.api.authorization.AuthorizationRequestToken;
import spotify.api.enums.AuthorizationScope;
import spotify.api.spotify.SpotifyApi;
import spotify.models.authorization.AuthorizationCodeFlowTokenResponse;
import java.util.*;

/**
 * Класс, создающий сессию Spotify Web API. Позволяет осуществлять
 * авторизацию методом Authorization Code Flow и обновлять токены,
 * ипользуя clientId и clientSecret.
 */
public class SpotifySession {
    private final static String clientId = System.getenv("SPOTIFY_CLIENT_ID");
    private final static String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
    private final static String redirectUri = "http://localhost:8080/auth/spotify/redirect";
    private String code;
    private AuthorizationCodeFlowTokenResponse token;

    public SpotifyApi spotifyApi;

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Создание редирект-ссылки с заданными разрешениями
     * @return redirect url
     */
    public String buildAuthorizationCodeFlow() {
        AuthorizationCodeFlow authorizationCodeFlow = new AuthorizationCodeFlow.Builder()
                .setClientId(clientId)
                .setRedirectUri(redirectUri)
                .setResponseType("code")
                .setScopes(Arrays.asList(
                        AuthorizationScope.USER_READ_CURRENTLY_PLAYING,
                        AuthorizationScope.PLAYLIST_MODIFY_PUBLIC,
                        AuthorizationScope.PLAYLIST_MODIFY_PRIVATE,
                        AuthorizationScope.USER_LIBRARY_READ
                ))
                .build();

        return authorizationCodeFlow.constructUrl();
    }

    public int getTokenExpiresIn() {
        return token.getExpiresIn();
    }

    /**
     * Получение AccessToken на 1 час по коду, полученному
     * по переходу по редирект-ссылке
     */
    public void buildAuthorizationRequestToken() {
        AuthorizationRequestToken authorizationRequestToken = new AuthorizationRequestToken();
        token = authorizationRequestToken
                .getAuthorizationCodeToken(
                        clientId,
                        clientSecret,
                        code,
                        redirectUri);
    }

    /**
     * Создание SpotifyApi для конкретного пользователя по
     * его токену.
     */
    public void buildSpotifyApi() {
        spotifyApi = new SpotifyApi(token.getAccessToken());
    }

    /**
     * Обновление истёкшего токена, используя
     * clientSecret и refreshToken
     */
    public void buildAuthorizationRefreshToken() {
        AuthorizationRefreshToken authorizationRefreshToken = new AuthorizationRefreshToken();
        token = authorizationRefreshToken.refreshAccessToken(
                clientId,
                clientSecret,
                token.getRefreshToken()
        );
    }

}
