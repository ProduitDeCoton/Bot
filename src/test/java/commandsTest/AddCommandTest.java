package commandsTest;


import commands.AddCommand;
import commands.ServiceCommand;
import logic.ActiveGroups;
import logic.ActiveUsers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import spotify.exceptions.SpotifyActionFailedException;
import spotifyTools.SpotifySession;

import static org.mockito.Mockito.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;


import commands.AddCommand;

/**
 * Тестирование реакции обработчика команды /add в разных случаях обращения.
 * /add добавляет трек в очередь проигрываемых.
 */
public class AddCommandTest {
    private final SpotifySession session = Mockito.mock(SpotifySession.class, RETURNS_DEEP_STUBS);

    /**
     * Попытка добавить трек в очередь, когда групповая сессия не создана.
     */
    @Test
    public void testNoGroupSession() {
        final AddCommand addCommand = spy(new AddCommand("/add", "."));

        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "group");
        final AbsSender absSender = mock(AbsSender.class, CALLS_REAL_METHODS);

        final String correctAnswer = """
                Групповая музыкальная сессия в этом чате не создана.

                Запустите групповую сессию при помощи команды /group""";

        try (final MockedStatic<ActiveGroups> mockActiveGroups = mockStatic(ActiveGroups.class)) {
            mockActiveGroups.when(() -> ActiveGroups.getGroupSession(chat)).thenReturn(null);

            doAnswer(invocationOnMock -> {
                Assert.fail();
                return null;
            }).when(addCommand).sendAnswer(eq(absSender), eq(chat.getId()), not(eq(correctAnswer)));

            addCommand.execute(absSender, user, chat, null);
        }
    }

    /**
     * Пользователь не авторизовался, пытается создать групповую сессию.
     */
    @Test
    public void testUnauthorizedUser() {
        final AddCommand addCommand = spy(new AddCommand("/add", "."));

        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "group");
        final AbsSender absSender = mock(AbsSender.class, CALLS_REAL_METHODS);

        final String correctAnswer = """
                Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.

                Для этого введите в чат со мной команду /auth""";

        ActiveGroups.createGroup(user, chat);

        try (final MockedStatic<ActiveUsers> activeUsersMocked = mockStatic(ActiveUsers.class)) {
            activeUsersMocked.when(() -> ActiveUsers.getSession(user)).thenReturn(null);

            doAnswer(invocationOnMock -> {
                Assert.fail();
                return null;
            }).when(addCommand).sendAnswer(eq(absSender), eq(chat.getId()), not(eq(correctAnswer)));

            addCommand.execute(absSender, user, chat, null);
        }
    }

    /**
     * У лидера чата нет премиум-подписки на спотифай.
     */
    @Test
    public void testNoPremium() {
        final AddCommand addCommand = spy(new AddCommand("/add", "."));

        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "group");
        final AbsSender absSender = mock(AbsSender.class, CALLS_REAL_METHODS);

        ActiveGroups.createGroup(user, chat);
        ActiveUsers.updateActiveUsers(user, session);

        when(session.getSpotifyApi().getCurrentUser().getProduct()).thenReturn("free");

        final String correctAnswer = """
                Похоже, у лидера отсутствует подписка Spotify Premium. Групповая сессия закрыта.

                Попробуйте создать группу с другим лидером, у которого оплачена подписка.""";

        doAnswer(invocationOnMock -> {
            Assert.fail();
            return null;
        }).when(addCommand).sendAnswer(eq(absSender), eq(chat.getId()), not(eq(correctAnswer)));

        addCommand.execute(absSender, user, chat, new String[]{"."});
    }
}