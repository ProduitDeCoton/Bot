package commandsTest;


import org.junit.Assert;
import org.junit.Test;

import commands.NonCommand;


public class NonCommandTest {
    private final String redirectUri = "http://localhost:8080/auth/spotify/redirect?code=";

    /**
     * Пустая строка
     */
    @Test
    public void test_empty() {
        final String test = "";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertNull(code);

        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * Непустой URI, но пустое значение у ключа code
     */
    @Test
    public void test_empty_code() {
        final String test = redirectUri;

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertNull(code);

        } catch (Exception e) {
            Assert.assertTrue(true);
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

        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * Правильный URI
     */
    @Test
    public void test_correct_uri() {
        final String test = redirectUri + "bb82338c_0ffe6666_f4f419fd";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertEquals("bb82338c_0ffe6666_f4f419fd", code);

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    /**
     * Правильный URI, есть ключ option помимо code.
     */
    @Test
    public void test_correct_uri_option() {
        final String test = redirectUri + "bb82338c_0ffe6666_f4f419fd&option=null";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertEquals("bb82338c_0ffe6666_f4f419fd", code);

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    /**
     * Разделитель &.
     * Относится к группе подразделителей (sub-delims).
     */
    public void test_sub_delim() {
        final String test = redirectUri + "bb8233&8c_0ffe6666_f4f419fd";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertEquals("bb8233", code);

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    /**
     * Разделитель @.
     * Состоит в группе главных разделителей (gen-delims).
     */
    @Test
    public void test_gen_delim() {
        final String test = redirectUri + "bb@82338c_0ffe6666_f4f4@19fd";

        try {
            final String code = NonCommand.getCode(test);
            Assert.assertEquals("bb", code);

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}