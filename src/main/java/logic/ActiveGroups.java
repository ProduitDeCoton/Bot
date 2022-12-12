package logic;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import spotifyTools.SpotifyGroup;
import spotifyTools.SpotifySession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Активные групповые сессии.
 */
public class ActiveGroups {
    // TODO: избавиться от static
    private static Map<Long, SpotifyGroup> activeGroups = new ConcurrentHashMap<>();

    public static void createGroup(User leader, Chat chat) {
        activeGroups.put(chat.getId(), new SpotifyGroup(leader));
    }

    public static SpotifyGroup getGroupSession(Chat chat) {
        return activeGroups.get(chat.getId());
    }

    public static void closeGroupSession(Chat chat) {
        activeGroups.remove(chat);
    }
}
