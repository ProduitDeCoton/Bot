package commandsTest;


import org.junit.Assert;
import org.junit.Test;

import commands.NonCommand;
import exceptions.WrongAuthRedirectUriException;

public class NonCommandTest {

    @Test
    public void test_null() {
        final String uri = null;

        try {
            final String code = NonCommand.getCode(uri);
            Assert.assertNull(code);

        } catch (WrongAuthRedirectUriException e) {

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Пустая строка
     */
    @Test
    public void test_empty() {
        final String uri = "";

        try {
            final String code = NonCommand.getCode(uri);
            Assert.assertNull(code);

        } catch (WrongAuthRedirectUriException e) {

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Правильная основа URI, параметры отсутствуют
     */
    @Test
    public void test_uri_without_code() {
        final String uri = System.getenv("SPOTIFY_REDIRECT_URI");

        try {
            final String code = NonCommand.getCode(uri);
            Assert.assertNull(code);

        } catch (WrongAuthRedirectUriException e) {

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Непустой корректный URI, но пустое значение у ключа code
     */
    @Test
    public void test_empty_code() {
        final String uri = System.getenv("SPOTIFY_REDIRECT_URI") + "?code=";

        try {
            final String code = NonCommand.getCode(uri);
            Assert.assertNull(code);

        } catch (WrongAuthRedirectUriException e) {

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Неправильный URI
     */
    @Test
    public void test_wrong_uri() {
        final String uri = "test";

        try {
            final String code = NonCommand.getCode(uri);
            Assert.assertNull(code);

        } catch (WrongAuthRedirectUriException e) {

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Правильный URI
     */
    @Test
    public void test_correct_uri() {
        final String uri = System.getenv("SPOTIFY_REDIRECT_URI") + "?code=" + "bb82338c_0ffe6666_f4f419fd";

        try {
            final String code = NonCommand.getCode(uri);
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
        final String uri = System.getenv("SPOTIFY_REDIRECT_URI") + "?code=" + "bb82338c_0ffe6666_f4f419fd&option=null";

        try {
            final String code = NonCommand.getCode(uri);
            Assert.assertEquals("bb82338c_0ffe6666_f4f419fd", code);

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Разделитель &.
     * Относится к группе подразделителей (sub-delims).
     */
    @Test
    public void test_sub_delim() {
        final String uri = System.getenv("SPOTIFY_REDIRECT_URI") + "?code=" + "bb8233&8c_0ffe6666_f4f419fd";

        try {
            final String code = NonCommand.getCode(uri);
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
        final String uri = System.getenv("SPOTIFY_REDIRECT_URI") + "?code=" + "bb@82338c_0ffe6666_f4f4@19fd";

        try {
            final String code = NonCommand.getCode(uri);
            Assert.assertEquals("bb", code);

        } catch (Exception e) {
            Assert.fail();
        }
    }
}