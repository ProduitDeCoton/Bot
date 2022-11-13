package inline_query_commands;

import logic.ActiveUsers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import spotify.models.playlists.PlaylistSimplified;
import spotify.models.tracks.SavedTrackFull;
import spotify.models.tracks.TrackFull;
import spotifyTools.SpotifySession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Тестирование класса, ответственного за обновление плейлиста любимых треков
 */
public class GetLikedSongsPlayListTest {
    private final SpotifySession session = Mockito.mock(SpotifySession.class, RETURNS_DEEP_STUBS);

    @Test
    public void testBuildPlaylist() {
        final var inlineQueryAnswer = new GetLikedSongsPlaylist();

        when(session.getTokenExpiresIn())
                .thenReturn(1000);

        final var user = new User(85865L, "testFirstName", false);
        ActiveUsers.updateActiveUsers(user, session);

        final List<PlaylistSimplified> playlists = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            final var playlist = new PlaylistSimplified();

            playlist.setName("playlist");
            playlist.setId(String.valueOf(i + 5000));

            playlists.add(playlist);
        }

        when(session.getSpotifyApi()
                .getPlaylists(nullable(Map.class))
                .getItems())
                .thenReturn(playlists);

        when(session.getSpotifyApi()
                .getCurrentUser()
                .getId())
                .thenReturn("1337");

        final List<SavedTrackFull> savedTracks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            final var savedTrack = new SavedTrackFull();
            final var track = new TrackFull();

            track.setName("track name %d".formatted(i));
            track.setUri("track link %d".formatted(i));

            savedTrack.setTrack(track);
            savedTracks.add(savedTrack);
        }

        when(session
                .getSpotifyApi()
                .getSavedTracks(nullable(Map.class))
                .getItems())
                .thenReturn(savedTracks);

        when(session
                .getSpotifyApi()
                .getSavedTracks(nullable(Map.class))
                .getTotal())
                .thenReturn(savedTracks.size());

        when(session
                .getSpotifyApi()
                .getPlaylist(nullable(String.class), nullable(Map.class))
                .getExternalUrls().getSpotify())
                .thenReturn("playlist link");

        final var result = (InlineQueryResultArticle) inlineQueryAnswer
                .constructInlineQueryResult(user, "test");

        final var resultMessage = (InputTextMessageContent) result.getInputMessageContent();
        final var resultString = resultMessage.getMessageText();

        Assert.assertEquals("[Любимые треки в Spotify](%s)"
                .formatted("playlist link"), resultString);
    }
}