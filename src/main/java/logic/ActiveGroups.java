package logic;

import org.telegram.telegrambots.meta.api.objects.User;
import spotifyTools.SpotifyGroup;
import spotifyTools.SpotifySession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveGroups {
    private static Map<Long, SpotifyGroup> activeGroups = new ConcurrentHashMap<>();

    public static void createGroup(User leader) {
        activeGroups.put(leader.getId(), new SpotifyGroup(leader));
    }

    public static SpotifyGroup getGroupSession(User leader) {
        return activeGroups.get(leader.getId());
    }
}
