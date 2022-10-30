package inline_query_commands;


import logic.ActiveUsers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.User;
import spotify_tools.SpotifySession;


public class GetLikedSongsPlaylistTest {

    @Test
    public void getSession() {
        var session = new SpotifySession();
        var user = new User(865L, "testFirstName", false);
        Assert.assertNull(ActiveUsers.getSession(user));

        ActiveUsers.updateActiveUsers(user, session);
        Assert.assertEquals(session, ActiveUsers.getSession(user));
    }

}
