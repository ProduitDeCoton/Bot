package inline_query_commands;

import logic.ActiveUsers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import spotify.models.artists.ArtistSimplified;
import spotify.models.generic.ExternalUrl;
import spotify.models.players.CurrentlyPlayingObject;
import spotify.models.tracks.TrackFull;
import spotifyTools.SpotifySession;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.Mockito.*;


public class GetCurrentPlayingObjectTest {
    private final SpotifySession session = Mockito.mock(SpotifySession.class, RETURNS_DEEP_STUBS);

    /**
     * Тест-кейс: пользователь не авторизовался в бота или
     * отозвал разрешение в параметрах Spotify.
     * Ожидаемый результат: в ответ на Inline-команду
     * выведется "Сейчас ничего не играет в Spotify"
     */
    @Test
    public void tryToCheckWhenSessionNull() {
        User user = new User(85865L, "testFirstName", false);
        var command = new GetCurrentPlayingObject();
        InlineQueryResultArticle result = (InlineQueryResultArticle) command.constructInlineQueryResult(user, "test");
        InputTextMessageContent resultMessage = (InputTextMessageContent) result.getInputMessageContent();
        var resultString = resultMessage.getMessageText();

        Assert.assertEquals(resultString, "Сейчас ничего не играет в Spotify");
    }

    /**
     * Тест-кейс: SpotifyApi получил доступ к данным пользователя
     * и пытается вывести текущий трек.
     * Ожидаемый редультат: в ответ на Inline-команду выведется
     * трек "Test track name" исполнителя "test artist"
     * c ссылками на трек и альбом "test link"
     */
    @Test
    public void testReturningInlineQueryResult() {
        var inlineQueryAnswer = new GetCurrentPlayingObject();
        User user = new User(85865L, "testFirstName", false);
        ActiveUsers.updateActiveUsers(user, session);

        TrackFull track = new TrackFull();
        track.setName("Test track name");
        track.setExternalUrls(new ExternalUrl());
        track.getExternalUrls().setSpotify("test link");
        track.setId("qwerty");

        ArrayList<ArtistSimplified> artists = new ArrayList<>();
        artists.add(new ArtistSimplified());
        artists.get(0).setName("test artist");

        var currentlyPlayingObject = new CurrentlyPlayingObject();
        currentlyPlayingObject.setItem(track);


        when(session.getTokenExpiresIn()).thenReturn(40);
        when(session.getSpotifyApi().getCurrentlyPlayedObject(any(Map.class)))
                .thenReturn(currentlyPlayingObject);
        when(session.getSpotifyApi().getTrack(nullable(String.class), nullable(Map.class)).getAlbum().getExternalUrls().getSpotify())
                .thenReturn("test link");
        when(session.getSpotifyApi().getTrack(any(String.class), any(Map.class)).getArtists())
                .thenReturn(artists);


        InlineQueryResultArticle result = (InlineQueryResultArticle) inlineQueryAnswer.constructInlineQueryResult(user, "test");
        InputTextMessageContent resultMessage = (InputTextMessageContent) result.getInputMessageContent();
        String resultString = resultMessage.getMessageText();
        Assert.assertEquals("Сейчас играет в Spotify:" + "\n" +
                "Test track name - test artist " + "\n\n" +
                "[Трек](test link) | [Альбом](test link)", resultString);
    }

}