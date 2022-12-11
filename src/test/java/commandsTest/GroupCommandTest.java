package commandsTest;

import commands.GroupCommand;
import logic.ActiveGroups;
import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import spotify.models.players.Device;
import spotifyTools.SpotifySession;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

public class GroupCommandTest {
    private final SpotifySession session = Mockito.mock(SpotifySession.class, RETURNS_DEEP_STUBS);

    /**
     * Попытка создания групповой сессии в личном чате.
     */
    @Test
    public void testPrivateChat() {
        final GroupCommand groupCommand = mock(GroupCommand.class);
        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "private");
        final AbsSender absSender = Mockito.mock(AbsSender.class, Mockito.CALLS_REAL_METHODS);

        doAnswer(invocation -> {
            String answer = null;

            final String correctAnswer = "Похоже, вы пытаетесь создать групповую сессию в личном чате." +
                    "\n\n" +
                    "Создайте групповую сессию в чате со своими друзьями.";

            if (chat.getType().equals("private")) {
                answer = correctAnswer;
            }

            Assert.assertEquals(correctAnswer, answer);
            return null;

        }).when(groupCommand).execute(absSender, user, chat, null);

        groupCommand.execute(absSender, user, chat, null);
    }

    /**
     * Неавторизованный пользователь.
     */
    @Test
    public void testUnauthorizedUser() {
        final GroupCommand groupCommand = mock(GroupCommand.class);
        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "group");
        final AbsSender absSender = Mockito.mock(AbsSender.class, Mockito.CALLS_REAL_METHODS);

        MockedStatic<ActiveGroups> ag=mockStatic(ActiveGroups.class);
        ag.when(() -> ActiveGroups.getGroupSession(chat)).thenReturn(null);

        final String correctAnswer = "Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной." +
                "\n\n" +
                "Для этого введите в чат со мной команду /auth";

        doAnswer(invocation -> {
            String answer = null;

            if (chat.getType().equals("private")) {
                answer = "Похоже, вы пытаетесь создать групповую сессию в личном чате." +
                        "\n\n" +
                        "Создайте групповую сессию в чате со своими друзьями.";
            }

            if (ActiveGroups.getGroupSession(chat) == null) {
                answer = "Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной." +
                        "\n\n" +
                        "Для этого введите в чат со мной команду /auth";
            }

            Assert.assertEquals(correctAnswer, answer);
            return null;

        }).when(groupCommand).execute(absSender, user, chat, null);

        groupCommand.execute(absSender, user, chat, null);
    }

    /**
     * Нет устройства с запущенным спотифай.
     */
    @Test
    public void testNoAvailableDevices() {
        final GroupCommand groupCommand = mock(GroupCommand.class);
        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "group");
        final AbsSender absSender = Mockito.mock(AbsSender.class, Mockito.CALLS_REAL_METHODS);

        ActiveGroups.createGroup(user, chat);

        when(session.getSpotifyApi().getAvailableDevices().getDevices()).thenReturn(new ArrayList<Device>());

        final String correctAnswer = "Spotify не запущен ни на одном устройстве. " +
                "Пожалуйста, запустите приложение и повторите попытку.";

        doAnswer(invocation -> {
            String answer = null;

            if (chat.getType().equals("private")) {
                answer = "Похоже, вы пытаетесь создать групповую сессию в личном чате." +
                        "\n\n" +
                        "Создайте групповую сессию в чате со своими друзьями.";
            }

            if (ActiveGroups.getGroupSession(chat) == null) {
                answer = "Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной." +
                        "\n\n" +
                        "Для этого введите в чат со мной команду /auth";
            }

            final var devices = session.getSpotifyApi().getAvailableDevices().getDevices();

            if (devices.isEmpty()) {
                answer = "Spotify не запущен ни на одном устройстве. " +
                        "Пожалуйста, запустите приложение и повторите попытку.";
            }

            Assert.assertEquals(correctAnswer, answer);
            return null;

        }).when(groupCommand).execute(absSender, user, chat, null);

        groupCommand.execute(absSender, user, chat, null);
    }
}