package logic;

import org.telegram.telegrambots.meta.api.objects.User;
import spotifyTools.SpotifySession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveUsers {
    private static Map<Long, SpotifySession> activeUsers = new ConcurrentHashMap<>();

    public static void updateActiveUsers(User user, SpotifySession session) {
        activeUsers.put(user.getId(), session);
    }

    public static SpotifySession getSession(User user) {
        return activeUsers.get(user.getId());
    }
}
