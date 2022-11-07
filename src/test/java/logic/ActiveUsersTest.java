package logic;

import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.User;
import spotifyTools.SpotifySession;

public class ActiveUsersTest {
    @Test
    public void checkAddSession() {
        var session = new SpotifySession();
        var user = new User(865L, "testFirstName", false);
        Assert.assertNull(ActiveUsers.getSession(user));

        ActiveUsers.updateActiveUsers(user, session);
        Assert.assertEquals(session, ActiveUsers.getSession(user));
    }
}