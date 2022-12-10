package commandsTest;


import commands.AddCommand;
import commands.GroupCommand;
import logic.ActiveUsers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import commands.NonCommand;
import exceptions.WrongAuthRedirectUriException;
import org.mockito.stubbing.OngoingStubbing;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import spotifyTools.SpotifySession;

import static org.mockito.Mockito.*;
import commands.GroupCommand;

public class GroupCommandTest {
    private final SpotifySession session = Mockito.mock(SpotifySession.class, RETURNS_DEEP_STUBS);

    @Test
    public void test_A() {
        final ActiveUsers activeUsers=mock(ActiveUsers.class);
        final GroupCommand groupCommand = mock(GroupCommand.class);
        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "private");
        final AbsSender absSender = Mockito.mock(AbsSender.class, Mockito.CALLS_REAL_METHODS);

        doAnswer(invocation -> {
            String answer = null;

            String correctAnswer = new StringBuilder()
                    .append("Похоже, вы пытаетесь создать групповую сессию в личном чате.")
                    .append("\n\n")
                    .append("Создайте групповую сессию в чате со своими друзьями.").toString();

            if (chat.getType().equals("private")) {
                answer = correctAnswer;
            } else {
                Assert.fail();
            }

            Assert.assertEquals(correctAnswer, answer);
            return null;

        }).when(groupCommand).execute(absSender, user, chat, null);

        groupCommand.execute(absSender, user, chat, null);
    }
}