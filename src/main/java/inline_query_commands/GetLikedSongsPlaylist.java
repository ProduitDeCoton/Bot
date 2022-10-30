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

/**
 * Формирование актуального плейлиста понравившихся песен.
 * Плейлист сохраняется в библиотеке под именем BOT_NAME PLAYLIST
 */
public class GetLikedSongsPlaylist extends InlineQueryCommand {

    private String getBotPlaylistName() {
        return "%s PLAYLIST".formatted(System.getenv("BOT_NAME"));
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
        final CreateUpdatePlaylistRequestBody body = new CreateUpdatePlaylistRequestBody(
                getBotPlaylistName(),
                "Плейлист генерируется автоматически и включает в себя " +
                        "первые 1000 сохранённых песен. Во избежание ошибок не меняйте его " +
                        "содержимое самостоятельно",
                true, false);

        session.spotifyApi.createPlaylist(session.spotifyApi.getCurrentUser().getId(), body);
    }

    private void deletePlaylistTracks(final String playlistId, final String playlistSnapshotId, final SpotifySession session) {
        final Map<String, String> getPlaylistTracksProperties = Map.ofEntries(entry("limit", "100"));

        int trackPosition = 0;
        Paging<PlaylistTrack> playlistTracks = null;
        boolean stopDelete = false;

        do {
            playlistTracks = session.spotifyApi.getPlaylistTracks(playlistId, getPlaylistTracksProperties);

            if (playlistTracks.getTotal() <= Integer.valueOf(getPlaylistTracksProperties.get("limit"))) {
                stopDelete = true;
            }

            var deleteTracks = new ArrayList<PlaylistItem>();

            for (var track : playlistTracks.getItems()) {
                deleteTracks.add(new PlaylistItem(track.getTrack().getUri(), new int[]{trackPosition}));
                trackPosition++;
            }

            final var deleteItemsPlaylistRequestBody = new DeleteItemsPlaylistRequestBody(deleteTracks, playlistSnapshotId);
            session.spotifyApi.deleteItemsFromPlaylist(playlistId, deleteItemsPlaylistRequestBody);

        } while (!stopDelete);
    }

    private void addPlaylistTracks(final String playlistId, final String playlistSnapshotId, final SpotifySession session) {
        final Map<String, String> getSavedTracksProperties = new HashMap<>();
        getSavedTracksProperties.put("limit", "50");
        getSavedTracksProperties.put("offset", "0");

        int trackCount = 0;
        boolean overLimit = false;

        for (int offset = 0; offset < session.spotifyApi.getSavedTracks(getSavedTracksProperties).getTotal(); offset += 50) {

            getSavedTracksProperties.put("offset", String.valueOf(offset));
            final var savedTracks = session.spotifyApi.getSavedTracks(getSavedTracksProperties).getItems();
            final var savedTracksUris = new ArrayList<String>();

            for (var track : savedTracks) {
                if (trackCount >= 1000) {
                    overLimit = true;
                    break;
                }

                trackCount++;
                savedTracksUris.add(track.getTrack().getUri());
            }

            if (overLimit) {
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