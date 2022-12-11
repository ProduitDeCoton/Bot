package commandsTest;


import commands.AddCommand;
import logic.ActiveGroups;
import logic.ActiveUsers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import spotify.exceptions.SpotifyActionFailedException;
import spotifyTools.SpotifySession;

import static org.mockito.Mockito.*;

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
        final AddCommand addCommand = mock(AddCommand.class);
        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "group");
        final AbsSender absSender = Mockito.mock(AbsSender.class, Mockito.CALLS_REAL_METHODS);

        try (MockedStatic<ActiveGroups> mockActiveGroups = mockStatic(ActiveGroups.class)) {
            mockActiveGroups.when(() -> ActiveGroups.getGroupSession(chat)).thenReturn(null);

            final String correctAnswer = new StringBuilder()
                    .append("Групповая музыкальная сессия в этом чате не создана.")
                    .append("\n\n")
                    .append("Запустите групповую сессию при помощи команды /group").toString();

            doAnswer(invocation -> {
                String answer = null;

                if (chat.getType().equals("private")) {
                    Assert.fail();
                }

                if (ActiveGroups.getGroupSession(chat) != null) {
                    Assert.fail();
                } else {
                    answer = new StringBuilder()
                            .append("Групповая музыкальная сессия в этом чате не создана.")
                            .append("\n\n")
                            .append("Запустите групповую сессию при помощи команды /group").toString();
                }

                Assert.assertEquals(correctAnswer, answer);
                return null;
            }).when(addCommand).execute(absSender, user, chat, null);
        }
    }

    /**
     * Пользователь не авторизовался, пытается создать групповую сессию.
     */
    @Test
    public void testUnauthorizedUser() {
        final AddCommand addCommand = mock(AddCommand.class);
        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "group");
        final AbsSender absSender = Mockito.mock(AbsSender.class, Mockito.CALLS_REAL_METHODS);

        ActiveGroups.createGroup(user, chat);

        try (MockedStatic<ActiveUsers> mockActiveUsers = mockStatic(ActiveUsers.class)) {
            mockActiveUsers.when(() -> ActiveUsers.getSession(user)).thenReturn(null);

            final String correctAnswer = new StringBuilder()
                    .append("Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.")
                    .append("\n\n")
                    .append("Для этого введите в чат со мной команду /auth").toString();

            doAnswer(invocation -> {
                String answer = null;

                if (!chat.getType().equals("group")) {
                    Assert.fail();
                }

                if (ActiveGroups.getGroupSession(chat) == null) {
                    Assert.fail();
                }

                if (ActiveUsers.getSession(user) != null) {
                    Assert.fail();
                } else {
                    answer = new StringBuilder()
                            .append("Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.")
                            .append("\n\n")
                            .append("Для этого введите в чат со мной команду /auth").toString();
                }

                Assert.assertEquals(correctAnswer, answer);
                return null;
            }).when(addCommand).execute(absSender, user, chat, null);
        }
    }

    @Test
    public void testNoPremium() {
        final AddCommand addCommand = mock(AddCommand.class);
        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "group");
        final AbsSender absSender = Mockito.mock(AbsSender.class, Mockito.CALLS_REAL_METHODS);

        ActiveGroups.createGroup(user, chat);
        ActiveUsers.updateActiveUsers(user, session);

        when(session.getSpotifyApi().getCurrentUser().getProduct()).thenReturn("free");

        final String correctAnswer = new StringBuilder()
                .append("Похоже, у лидера отсутствует подписка Spotify Premium. ")
                .append("Групповая сессия закрыта.")
                .append("\n\n")
                .append("Попробуйте создать группу с другим лидером, у которого оплачена подписка.").toString();

        doAnswer(invocation -> {
            String answer = null;

            if (chat.getType().equals("private")) {
                Assert.fail();
            }

            if (ActiveGroups.getGroupSession(chat) == null) {
                Assert.fail();
            }

            if (ActiveUsers.getSession(user) == null) {
                Assert.fail();
            }

            try {
                if (session.getSpotifyApi().getCurrentUser().getProduct().equals("premium")) {
                    Assert.fail();
                }

                throw new SpotifyActionFailedException(".");

            } catch (final SpotifyActionFailedException e) {
                answer = new StringBuilder()
                        .append("Похоже, у лидера отсутствует подписка Spotify Premium. ")
                        .append("Групповая сессия закрыта.")
                        .append("\n\n")
                        .append("Попробуйте создать группу с другим лидером, у которого оплачена подписка.").toString();
            }

            Assert.assertEquals(correctAnswer, answer);
            return null;
        }).when(addCommand).execute(absSender, user, chat, null);
    }
}