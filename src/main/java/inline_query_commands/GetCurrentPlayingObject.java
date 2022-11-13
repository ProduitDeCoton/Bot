package inline_query_commands;

import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import spotify.exceptions.SpotifyActionFailedException;
import spotify.models.players.CurrentlyPlayingObject;
import spotifyTools.SpotifySession;

import java.util.HashMap;
import java.util.Map;

/**
 * Inline-команда, позволяющая посмотреть текущий
 * воспроизводимый трек
 */
public class GetCurrentPlayingObject extends InlineQueryCommand {

    /**
     * Возвращает объект "текущий воспроизводимый трек".
     * Возвращает null, если сессия не задана.
     *
     * @param user текущий Telegram-пользователь
     * @return CurrentlyPlayingObject
     */
    private CurrentlyPlayingObject getCurrentPlayingObject(User user) {
        SpotifySession spotifySession = ActiveUsers.getSession(user);
        if (spotifySession == null) return null;

        if (spotifySession.getTokenExpiresIn() <= 30) {
            spotifySession.authorizeByRefreshToken();
        }

        Map<String, String> properties = new HashMap<>();
        try {
            return spotifySession.getSpotifyApi().getCurrentlyPlayedObject(properties);
        } catch (SpotifyActionFailedException e) {
            return null;
        }
    }

    /**
     * Возвращает строку с названием трека.
     * Возвращает null, если трек есть null
     * @param object CurrentlyPlayingObject
     * @return String
     */
    private String getCurrentPlayingTrackName(CurrentlyPlayingObject object) {
        if (object == null) return null;
        return object.getItem().getName();
    }

    /**
     * Возвращает ссылку на трек.
     * Возвращает null, если трек есть null
     * @param object CurrentlyPlayingObject
     * @return String
     */
    private String getCurrentPlayingTrackLink(CurrentlyPlayingObject object) {
        if (object == null) return null;
        String link = object.getItem().getExternalUrls().getSpotify();
        return "[Трек](" + link + ")";
    }

    /**
     * Возвращает ссылку на альбом, в котором находится трек.
     * Возвращает null, если не задана сессия в Spotify
     * или если трек есть null
     * @param object CurrentlyPlayingObject
     * @param user текущий Telegram-пользователь
     * @return String
     */
    private String getCurrentPlayingAlbumLink(CurrentlyPlayingObject object, User user) {
        SpotifySession spotifySession = ActiveUsers.getSession(user);
        if (spotifySession == null || object == null) return null;
        var trackId = object.getItem().getId();
        Map<String, String> properties = new HashMap<>();
        var link = spotifySession.getSpotifyApi().getTrack(trackId, properties).getAlbum().getExternalUrls().getSpotify();

        return "[Альбом](" + link + ")";
    }

    /**
     * Возвращает строку с именами всех исполнителей трека.
     * Возвращает null, если не задана сессия в Spotify
     * или трек есть null
     * @param track CurrentlyPlayingObject
     * @param user текущий Telegram-пользователь
     * @return String
     */
    private String getCurrentPlayingArtistName(CurrentlyPlayingObject track, User user) {
        SpotifySession spotifySession = ActiveUsers.getSession(user);
        if (spotifySession == null || track == null) return null;
        var trackId = track.getItem().getId();
        Map<String, String> properties = new HashMap<>();
        var artistsList = spotifySession.getSpotifyApi().getTrack(trackId, properties).getArtists();

        StringBuilder artists = new StringBuilder();
        for (var artist : artistsList) {
            artists.append(artist.getName());
            artists.append(", ");
        }
        artists.delete(artists.length() - 2, artists.length() - 1);
        return artists.toString();
    }

    /**
     * Возвращает InlineQueryResultArticle, готовый к использованию в
     * Inline-режиме. Содержит в себе информацию о текущем воспроизводимом треке.
     * @param user текущий Telegram-пользователь
     * @param showableInlineQueryText текст, который показывается во время показа inlineQuery
     * @return InlineQueryResultArticle
     */
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
