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
 * Тестирование реакции обработчика команды /skip.
 * /skip позволяет пройти через трек (пропустить, скипнуть) в очереди прослушиваемых.
 */
public class SkipCommandTest {

    private final SpotifySession session = Mockito.mock(SpotifySession.class, RETURNS_DEEP_STUBS);

    /**
     * Попытка пропустить трек в очереди при отсутствии групповой сессии.
     */
    @Test
    public void testNoGroupSession() {
        final AddCommand addCommand = mock(AddCommand.class);
        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "group");
        final AbsSender absSender = Mockito.mock(AbsSender.class, Mockito.CALLS_REAL_METHODS);

        try (MockedStatic<ActiveGroups> mockActiveGroups = mockStatic(ActiveGroups.class)) {
            mockActiveGroups.when(() -> ActiveGroups.getGroupSession(chat)).thenReturn(null);

            final String correctAnswer = """
                    Групповая музыкальная сессия в этом чате не создана.

                    Запустите групповую сессию при помощи команды /group""";

            doAnswer(invocation -> {
                String answer = null;

                if (chat.getType().equals("private")) {
                    Assert.fail();
                }

                if (ActiveGroups.getGroupSession(chat) != null) {
                    Assert.fail();
                } else {
                    answer = """
                            Групповая музыкальная сессия в этом чате не создана.

                            Запустите групповую сессию при помощи команды /group""";
                }

                Assert.assertEquals(correctAnswer, answer);
                return null;
            }).when(addCommand).execute(absSender, user, chat, null);
        }
    }

    /**
     * Пользователь не авторизовался, пытается пропустить трек.
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

            final String correctAnswer = """
                    Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.

                    Для этого введите в чат со мной команду /auth""";

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
                    answer = """
                            Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.
                                                        
                            Для этого введите в чат со мной команду /auth""";
                }

                Assert.assertEquals(correctAnswer, answer);
                return null;
            }).when(addCommand).execute(absSender, user, chat, null);
        }
    }

    /**
     * У пользователя отсутствует подписка premium.
     * Пользователь общается с ботом лично.
     */
    @Test
    public void testNoPremiumPrivateChat() {
        final AddCommand addCommand = mock(AddCommand.class);
        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "private");
        final AbsSender absSender = Mockito.mock(AbsSender.class, Mockito.CALLS_REAL_METHODS);

        ActiveGroups.createGroup(user, chat);
        ActiveUsers.updateActiveUsers(user, session);

        when(session.getSpotifyApi().getCurrentUser().getProduct()).thenReturn("free");

        final String correctAnswer = """
                Похоже, у вас отсутствует подписка Spotify Premium.

                Продлите срок действия подписки и попробуйте ещё раз.""";

        doAnswer(invocation -> {
            String answer = null;

            if (!chat.getType().equals("private")) {
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
                answer = """
                        Похоже, у вас отсутствует подписка Spotify Premium.

                        Продлите срок действия подписки и попробуйте ещё раз.""";
            }

            Assert.assertEquals(correctAnswer, answer);
            return null;
        }).when(addCommand).execute(absSender, user, chat, null);
    }


    /**
     * У пользователя отсутствует подписка premium.
     * Пользователь общается с ботом в групповом чате.
     */
    @Test
    public void testNoPremiumGroupChat() {
        final AddCommand addCommand = mock(AddCommand.class);
        final User user = new User(1000L, "Dummy", false);
        final Chat chat = new Chat(1000L, "group");
        final AbsSender absSender = Mockito.mock(AbsSender.class, Mockito.CALLS_REAL_METHODS);

        final String correctAnswer = """
                Похоже, у лидера отсутствует подписка Spotify Premium. Групповая сессия закрыта.

                Попробуйте создать группу с другим лидером, у которого оплачена подписка.""";

        doAnswer(invocation -> {
            String answer = null;

            if (!chat.getType().equals("group")) {
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
                answer = """
                        Похоже, у лидера отсутствует подписка Spotify Premium. Групповая сессия закрыта.

                        Попробуйте создать группу с другим лидером, у которого оплачена подписка.""";
            }

            Assert.assertEquals(correctAnswer, answer);
            return null;
        }).when(addCommand).execute(absSender, user, chat, null);
    }
}
