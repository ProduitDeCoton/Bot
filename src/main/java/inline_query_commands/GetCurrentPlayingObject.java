package inline_query_commands;

import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import spotify.exceptions.SpotifyActionFailedException;
import spotify.models.players.CurrentlyPlayingObject;
import spotify_tools.SpotifySession;

import java.util.HashMap;
import java.util.Map;

public class GetCurrentPlayingObject extends InlineQueryCommand {

    private CurrentlyPlayingObject getCurrentPlayingObject(User user) {
        SpotifySession spotifySession = ActiveUsers.getSession(user);
        if (spotifySession == null) return null;

        if (spotifySession.getTokenExpiresIn() <= 30) {
            spotifySession.buildAuthorizationRefreshToken();
            spotifySession.buildSpotifyApi();
        }

        Map<String, String> properties = new HashMap<>();
        try {
            return spotifySession.spotifyApi.getCurrentlyPlayedObject(properties);
        } catch (SpotifyActionFailedException e) {
            return null;
        }
    }


    private String getCurrentPlayingTrackName(CurrentlyPlayingObject object) {
        if (object == null) return null;
        return object.getItem().getName();
    }


    private String getCurrentPlayingTrackLink(CurrentlyPlayingObject object) {
        if (object == null) return null;
        String link = object.getItem().getExternalUrls().getSpotify();
        return "[Трек](" + link + ")";
    }


    private String getCurrentPlayingAlbumLink(CurrentlyPlayingObject object, User user) {
        SpotifySession spotifySession = ActiveUsers.getSession(user);
        if (spotifySession == null || object == null) return null;
        var trackId = object.getItem().getId();
        Map<String, String> properties = new HashMap<>();
        var link = spotifySession.spotifyApi.getTrack(trackId, properties).getAlbum().getExternalUrls().getSpotify();

        return "[Альбом](" + link + ")";
    }


    private String getCurrentPlayingArtistName(CurrentlyPlayingObject track, User user) {
        SpotifySession spotifySession = ActiveUsers.getSession(user);
        if (spotifySession == null || track == null) return null;
        var trackId = track.getItem().getId();
        Map<String, String> properties = new HashMap<>();
        var artistsList = spotifySession.spotifyApi.getTrack(trackId, properties).getArtists();

        StringBuilder artists = new StringBuilder();
        for (var artist : artistsList) {
            artists.append(artist.getName());
            artists.append(", ");
        }
        artists.delete(artists.length() - 2, artists.length() - 1);
        return artists.toString();
    }

    @Override
    public InlineQueryResult constructInlineQueryResult(User user, String showableInlineQueryText) {
        CurrentlyPlayingObject currentlyPlayingObject = getCurrentPlayingObject(user);
        if (currentlyPlayingObject == null) {
            InputTextMessageContent answerMessage = buildAnswerMessage("Сейчас ничего не играет в Spotify");
            return new InlineQueryResultArticle("CURRENT_PLAYING", showableInlineQueryText, answerMessage);
        }

        String trackName = getCurrentPlayingTrackName(currentlyPlayingObject);
        String artistsNames = getCurrentPlayingArtistName(currentlyPlayingObject, user);
        String trackLink = getCurrentPlayingTrackLink(currentlyPlayingObject);
        String albumLink = getCurrentPlayingAlbumLink(currentlyPlayingObject, user);

        InputTextMessageContent answerMessage = buildAnswerMessage("Сейчас играет в Spotify:" + "\n" +
                trackName + " - " + artistsNames + "\n\n" +
                trackLink + " | " + albumLink);

        return new InlineQueryResultArticle("CURRENT_PLAYING", showableInlineQueryText, answerMessage);
    }
}
