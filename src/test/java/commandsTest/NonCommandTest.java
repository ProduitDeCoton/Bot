package commandsTest;

import org.junit.Assert;
import org.junit.Test;

import commands.NonCommand;
import exceptions.WrongAuthRedirectUriException;
import org.telegram.telegrambots.meta.api.objects.User;

public class NonCommandTest {
    final String uriBase = "http://localhost:8080/auth/spotify/redirect";

    @Test
    public void checkWhenTextNull() {
        NonCommand nonCommand = new NonCommand();
        User user = new User(843L, "testFirstName", false);
        String expectedAnswer = "Простите, я не понимаю Вас. Похоже, что Вы ввели сообщение, " +
                "не соответствующее формату. Возможно, Вам поможет /help";

        Assert.assertEquals(expectedAnswer, nonCommand.nonCommandExecute(user, null));
    }

    /**
     * Тест на null.
     */
    @Test(expected = WrongAuthRedirectUriException.class)
    public void testNull() throws WrongAuthRedirectUriException {
        NonCommand.getCode(null);
    }

    /**
     * Проверка на пустую строку.
     */
    @Test(expected = WrongAuthRedirectUriException.class)
    public void testEmpty() throws WrongAuthRedirectUriException {
        NonCommand.getCode("");
    }

    /**
     * Правильная основа URI, параметры отсутствуют.
     */
    @Test(expected = WrongAuthRedirectUriException.class)
    public void testUriBaseOnly() throws WrongAuthRedirectUriException {
        NonCommand.getCode(uriBase);
    }

    /**
     * Непустой корректный URI, но пустое значение у ключа code.
     */
    @Test(expected = WrongAuthRedirectUriException.class)
    public void testEmptyAuthCode() throws WrongAuthRedirectUriException {
        NonCommand.getCode(uriBase + "?code=");
    }

    /**
     * Неправильный URI.
     */
    @Test(expected = WrongAuthRedirectUriException.class)
    public void testWrongUri() throws WrongAuthRedirectUriException {
        NonCommand.getCode("test");
    }

    /**
     * Правильный URI, присутствует параметр code.
     */
    @Test
    public void testCorrectUri() {
        try {
            final String uri = uriBase + "?code=bb82338c_0ffe6666_f4f419fd";
            Assert.assertEquals("bb82338c_0ffe6666_f4f419fd", NonCommand.getCode(uri));

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Правильный URI, в строке помимо code есть ключ option.
     */
    @Test
    public void testCorrectUriWithOption() {
        try {
            final String uri = uriBase + "?code=bb82338c_0ffe6666_f4f419fd&option=null";
            Assert.assertEquals("bb82338c_0ffe6666_f4f419fd", NonCommand.getCode(uri));

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Проверка на разделитель &, относящийся к группе подразделителей (sub-delims).
     */
    @Test
    public void testUriWithSubDelim() {
        try {
            final String uri = uriBase + "?code=bb8233&8c_0ffe6666_f4f419fd";
            Assert.assertEquals("bb8233", NonCommand.getCode(uri));

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Проверка на разделитель @, состоящий в группе главных разделителей (gen-delims).
     */
    @Test
    public void testUriWithGenDelim() {
        try {
            final String uri = uriBase + "?code=bb@82338c_0ffe6666_f4f4@19fd";
            Assert.assertEquals("bb", NonCommand.getCode(uri));

        } catch (Exception e) {
            Assert.fail();
        }
    }
}