package spotify_tools;

import spotify.api.authorization.AuthorizationCodeFlow;
import spotify.api.authorization.AuthorizationRefreshToken;
import spotify.api.authorization.AuthorizationRequestToken;
import spotify.api.enums.AuthorizationScope;
import spotify.api.spotify.SpotifyApi;
import spotify.models.authorization.AuthorizationCodeFlowTokenResponse;
import spotify.models.paging.Paging;
import spotify.models.playlists.PlaylistItem;
import spotify.models.playlists.PlaylistTrack;
import spotify.models.playlists.requests.CreateUpdatePlaylistRequestBody;
import spotify.models.playlists.requests.DeleteItemsPlaylistRequestBody;
import spotify.models.playlists.requests.ReplacePlaylistItemsRequestBody;
import spotify.models.tracks.TrackFull;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

        var playlists = spotifyApi.getPlaylists(null).getItems();
        String botPlayListName = "@spotify_now_bot PLAYLIST";
        boolean botPlaylistExist = false;

        for (var playlist : playlists) {
            if (playlist.getName().equals(botPlayListName)) {
                botPlaylistExist = true;
                break;
            }

            System.out.println(playlist.getName());
        }

        System.out.println(botPlaylistExist);

        if (!botPlaylistExist) {
            CreateUpdatePlaylistRequestBody body;
            body = new CreateUpdatePlaylistRequestBody(botPlayListName, "TEST_DESC", true, false);
            spotifyApi.createPlaylist(spotifyApi.getCurrentUser().getId(), body);
        }

        String botPlaylistId = "";
        String botPlaylistSnapshotId = "";

        for (var playlist : spotifyApi.getPlaylists(null).getItems()) {
            if (playlist.getName().equals(botPlayListName)) {
                botPlaylistId = playlist.getId();
                botPlaylistSnapshotId = playlist.getSnapshotId();
                break;
            }
        }

        Map<String, String> getPlaylistTracksProperties = new HashMap<>();
        getPlaylistTracksProperties.put("limit", "100");

        int pos = 0;
        Paging<PlaylistTrack> playlistTracks = null;

        // Удаление из ботовского плейлиста
        boolean exit = false;
        do {
            playlistTracks = spotifyApi.getPlaylistTracks(botPlaylistId, getPlaylistTracksProperties);

            if (playlistTracks.getTotal() <= Integer.valueOf(getPlaylistTracksProperties.get("limit"))) {
                exit = true;
            }

            var deleteTracks = new ArrayList<PlaylistItem>();

            for (var track : playlistTracks.getItems()) {
                System.out.println(track.getTrack().getName());
                deleteTracks.add(new PlaylistItem(track.getTrack().getUri(), new int[]{pos}));
                pos++;
            }

            DeleteItemsPlaylistRequestBody deleteItemsPlaylistRequestBody = new DeleteItemsPlaylistRequestBody(deleteTracks, botPlaylistSnapshotId);
            spotifyApi.deleteItemsFromPlaylist(botPlaylistId, deleteItemsPlaylistRequestBody);

            System.out.println(pos);

        } while (!exit);

        // Добавление в ботовский плейлист
        Map<String, String> getSavedTracksProperties = new HashMap<>();
        getSavedTracksProperties.put("limit",  "50");
        getSavedTracksProperties.put("offset", "0");

        // System.out.println(botPlaylistId);

        for (int offset = 0; offset < spotifyApi.getSavedTracks(getSavedTracksProperties).getTotal(); offset += 50) {

            getSavedTracksProperties.put("offset", String.valueOf(offset));
            var savedTracks = spotifyApi.getSavedTracks(getSavedTracksProperties).getItems();

            List<String> savedTracksUris = new ArrayList<>();

            for (var track : savedTracks) {
                savedTracksUris.add(track.getTrack().getUri());
            }

            System.out.println(offset);
            spotifyApi.addItemsToPlaylist(savedTracksUris, botPlaylistId, offset);
        }
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
