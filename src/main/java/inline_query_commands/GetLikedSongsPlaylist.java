package inline_query_commands;

import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import spotify.models.paging.Paging;
import spotify.models.playlists.PlaylistItem;
import spotify.models.playlists.PlaylistTrack;
import spotify.models.playlists.requests.CreateUpdatePlaylistRequestBody;
import spotify.models.playlists.requests.DeleteItemsPlaylistRequestBody;
import spotify_tools.SpotifySession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLikedSongsPlaylist extends InlineQueryCommand {

    public SpotifySession getSession(User user) {
        SpotifySession spotifySession = ActiveUsers.getSession(user);
        if (spotifySession == null) return null;

        if (spotifySession.getTokenExpiresIn() <= 30) {
            spotifySession.buildAuthorizationRefreshToken();
            spotifySession.buildSpotifyApi();
            ActiveUsers.updateActiveUsers(user, spotifySession);
        }
        return spotifySession;
    }

    public String buildPlaylist(User user){
        SpotifySession session = ActiveUsers.getSession(user);
        var playlists = session.spotifyApi.getPlaylists(null).getItems();
        String botPlayListName = "@spotify_now_bot PLAYLIST";
        boolean botPlaylistExist = false;

        for (var playlist : playlists) {
            if (playlist.getName().equals(botPlayListName)) {
                botPlaylistExist = true;
                break;
            }

        }


        if (!botPlaylistExist) {
            CreateUpdatePlaylistRequestBody body;
            body = new CreateUpdatePlaylistRequestBody(botPlayListName, "Плейлист генерируется автоматически и включает в себя первые 1000 сохранённых песен. Во избежание ошибок не меняйте его содержимое самостоятельно", true, false);
            session.spotifyApi.createPlaylist(session.spotifyApi.getCurrentUser().getId(), body);
        }

        String botPlaylistId = "";
        String botPlaylistSnapshotId = "";

        for (var playlist : session.spotifyApi.getPlaylists(null).getItems()) {
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
            playlistTracks = session.spotifyApi.getPlaylistTracks(botPlaylistId, getPlaylistTracksProperties);

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
            session.spotifyApi.deleteItemsFromPlaylist(botPlaylistId, deleteItemsPlaylistRequestBody);


        } while (!exit);

        // Добавление в ботовский плейлист
        Map<String, String> getSavedTracksProperties = new HashMap<>();
        getSavedTracksProperties.put("limit", "50");
        getSavedTracksProperties.put("offset", "0");

        int count = 0;
        exit = false;
        for (int offset = 0; offset < session.spotifyApi.getSavedTracks(getSavedTracksProperties).getTotal(); offset += 50) {

            getSavedTracksProperties.put("offset", String.valueOf(offset));
            var savedTracks = session.spotifyApi.getSavedTracks(getSavedTracksProperties).getItems();

            List<String> savedTracksUris = new ArrayList<>();

            for (var track : savedTracks) {
                if (count >= 1000) {
                    exit = true;
                    break;
                }
                count++;
                savedTracksUris.add(track.getTrack().getUri());
            }
            if (exit) {
                break;
            }

            session.spotifyApi.addItemsToPlaylist(savedTracksUris, botPlaylistId, offset);
        }
        return session.spotifyApi.getPlaylist(botPlaylistId, null).getExternalUrls().getSpotify();
    }



    @Override
    public InlineQueryResult constructInlineQueryResult(User user, String showableInlineQueryText) {
        InputTextMessageContent answerMessage = buildAnswerMessage("на ссылку" + buildPlaylist(user));
        return new InlineQueryResultArticle("LIKED_SONGS", showableInlineQueryText, answerMessage);
    }

}
