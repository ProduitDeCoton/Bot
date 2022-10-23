package commandsTest;


import org.junit.Assert;
import org.junit.Test;

import commands.NonCommand;
import exceptions.WrongAuthRedirectUriException;

public class NonCommandTest {

    /**
     * Пустая строка
     */
    @Test
    public void test_empty() {
        final String test = "";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertNull(code);

        } catch (WrongAuthRedirectUriException e) {
            Assert.assertTrue(true);

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Непустой корректный URI, но пустое значение у ключа code
     */
    @Test
    public void test_empty_code() {
        final String test = System.getenv("SPOTIFY_REDIRECT_URI");

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertNull(code);

        } catch (WrongAuthRedirectUriException e) {
            Assert.assertTrue(true);

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    /**
     * Неправильный URI
     */
    @Test
    public void test_wrong_uri() {
        final String test = "test";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertNull(code);

        } catch (WrongAuthRedirectUriException e) {
            Assert.assertTrue(true);

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    /**
     * Правильный URI
     */
    @Test
    public void test_correct_uri() {
        final String test = System.getenv("SPOTIFY_REDIRECT_URI") + "?code=" + "bb82338c_0ffe6666_f4f419fd";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertEquals("bb82338c_0ffe6666_f4f419fd", code);

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Правильный URI, есть ключ option помимо code.
     */
    @Test
    public void test_correct_uri_option() {
        final String test = System.getenv("SPOTIFY_REDIRECT_URI") + "?code=" + "bb82338c_0ffe6666_f4f419fd&option=null";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertEquals("bb82338c_0ffe6666_f4f419fd", code);

        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    /**
     * Разделитель &.
     * Относится к группе подразделителей (sub-delims).
     */
    public void test_sub_delim() {
        final String test = System.getenv("SPOTIFY_REDIRECT_URI") + "?code=" + "bb8233&8c_0ffe6666_f4f419fd";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertEquals("bb8233", code);

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Разделитель @.
     * Состоит в группе главных разделителей (gen-delims).
     */
    @Test
    public void test_gen_delim() {
        final String test = System.getenv("SPOTIFY_REDIRECT_URI") + "?code=" + "bb@82338c_0ffe6666_f4f4@19fd";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertEquals("bb", code);

        } catch (Exception e) {
            Assert.fail();
        }
    }
}