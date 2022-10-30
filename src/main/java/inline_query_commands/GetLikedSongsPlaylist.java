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

import static java.util.Map.entry;

public class GetLikedSongsPlaylist extends InlineQueryCommand {

    private String getBotPlaylistName() {
        return "@spotify_now_bot PLAYLIST";
    }

    private boolean botPlaylistExist(final SpotifySession session) {
        final var playlists = session.spotifyApi
                .getPlaylists(null)
                .getItems();

        for (final var playlist : playlists) {
            if (playlist.getName().equals(getBotPlaylistName())) {
                return true;
            }
        }

        return false;
    }

    private String getBotPlaylistId(final SpotifySession session) {
        for (final var playlist : session.spotifyApi.getPlaylists(null).getItems()) {
            if (playlist.getName().equals(getBotPlaylistName())) {
                return playlist.getId();
            }
        }

        return null;
    }
    private String getBotPlaylistSnapshotId(final SpotifySession session) {
        for (final var playlist : session.spotifyApi.getPlaylists(null).getItems()) {
            if (playlist.getName().equals(getBotPlaylistName())) {
                return playlist.getSnapshotId();
            }
        }

        return null;
    }

    private void createBotPlayList(final SpotifySession session) {
        CreateUpdatePlaylistRequestBody body;
        body = new CreateUpdatePlaylistRequestBody(getBotPlaylistName(), "Плейлист генерируется автоматически и включает в себя первые 1000 сохранённых песен. Во избежание ошибок не меняйте его содержимое самостоятельно", true, false);

        session.spotifyApi.createPlaylist(session.spotifyApi.getCurrentUser().getId(), body);
    }

    private void deletePlaylistTracks(final String playlistId, final String playlistSnapshotId, final SpotifySession session) {
        final Map<String, String> getPlaylistTracksProperties = Map.ofEntries(entry("limit", "100"));

        int pos = 0;
        Paging<PlaylistTrack> playlistTracks = null;
        boolean exit = false;

        do {
            playlistTracks = session.spotifyApi.getPlaylistTracks(playlistId, getPlaylistTracksProperties);

            if (playlistTracks.getTotal() <= Integer.valueOf(getPlaylistTracksProperties.get("limit"))) {
                exit = true;
            }

            var deleteTracks = new ArrayList<PlaylistItem>();

            for (var track : playlistTracks.getItems()) {
                System.out.println(track.getTrack().getName());
                deleteTracks.add(new PlaylistItem(track.getTrack().getUri(), new int[]{pos}));
                pos++;
            }

            DeleteItemsPlaylistRequestBody deleteItemsPlaylistRequestBody = new DeleteItemsPlaylistRequestBody(deleteTracks, playlistSnapshotId);
            session.spotifyApi.deleteItemsFromPlaylist(playlistId, deleteItemsPlaylistRequestBody);

        } while (!exit);
    }

    private void addPlaylistTracks(final String playlistId, final String playlistSnapshotId, final SpotifySession session) {
        Map<String, String> getSavedTracksProperties = new HashMap<>();
        getSavedTracksProperties.put("limit", "50");
        getSavedTracksProperties.put("offset", "0");

        int count = 0;
        boolean exit = false;

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

            session.spotifyApi.addItemsToPlaylist(savedTracksUris, playlistId, offset);
        }
    }

    private void updateBotPlaylist(final String playlistId, final String playlistSnapshotId, final SpotifySession session) {
        deletePlaylistTracks(playlistId, playlistSnapshotId, session);
        addPlaylistTracks(playlistId, playlistSnapshotId, session);
    }

    public SpotifySession getSession(final User user) {
        final var spotifySession = ActiveUsers.getSession(user);

        if (spotifySession == null) {
            return null;
        }

        if (spotifySession.getTokenExpiresIn() <= 30) {
            spotifySession.buildAuthorizationRefreshToken();
            spotifySession.buildSpotifyApi();

            ActiveUsers.updateActiveUsers(user, spotifySession);
        }

        return spotifySession;
    }

    public String buildPlaylist(final User user) {
        final var session = getSession(user);

        if (!botPlaylistExist(session)) {
            createBotPlayList(session);
        }

        final var botPlaylistId = getBotPlaylistId(session);
        final var botPlaylistSnapshotId = getBotPlaylistSnapshotId(session);

        updateBotPlaylist(botPlaylistId, botPlaylistSnapshotId, session);

        return session.spotifyApi.getPlaylist(botPlaylistId, null).getExternalUrls().getSpotify();
    }

    @Override
    public InlineQueryResult constructInlineQueryResult(User user, String showableInlineQueryText) {
        InputTextMessageContent answerMessage = buildAnswerMessage("на ссылку" + buildPlaylist(user));
        return new InlineQueryResultArticle("LIKED_SONGS", showableInlineQueryText, answerMessage);
    }
}