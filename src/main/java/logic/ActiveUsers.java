package logic;

import org.telegram.telegrambots.meta.api.objects.User;
import spotify_tools.SpotifySession;

import java.util.HashMap;
import java.util.Map;

public class ActiveUsers {
    private static Map<User, SpotifySession> activeUsers = new HashMap<>();

    public static void updateActiveUsers(User user, SpotifySession session) {
        activeUsers.put(user, session);
    }

    public static SpotifySession getSession(User user) {
        return activeUsers.get(user);
    }
}
