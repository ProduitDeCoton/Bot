package logic;

import org.telegram.telegrambots.meta.api.objects.User;
import spotify_tools.SpotifySession;

import java.util.HashMap;
import java.util.Map;

public class ActiveUsers {
    private static Map<Long, SpotifySession> activeUsers = new HashMap<Long, SpotifySession>();

    public static void updateActiveUsers(User user, SpotifySession session) {
        activeUsers.put(user.getId(), session);
    }

    public static SpotifySession getSession(User user) {
        return activeUsers.get(user.getId());
    }
}
