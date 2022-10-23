package commandsTest;


import org.junit.Assert;
import org.junit.Test;

import commands.NonCommand;


public class NonCommandTest {
    private final String redirectUri = "http://localhost:8080/auth/spotify/redirect?code=";

    @Test
    public void test_1() {
        final String test = "";

        try {
            final String s = NonCommand.getCode(test);
            Assert.assertEquals(null, s);

        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void test_2() {
        final String test = redirectUri;

        try {
            final String s = NonCommand.getCode(test);
            Assert.assertEquals(null, s);

        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void test_3() {
        final String test = "test";

        try {
            final String s = NonCommand.getCode(test);
            Assert.assertEquals(null, s);

        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void test_4() {
        final String test = redirectUri + "bb82338c_0ffe6666_f4f419fd";

        try {
            final String s = NonCommand.getCode(test);
            Assert.assertEquals("bb82338c_0ffe6666_f4f419fd", s);

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void test_5() {
        final String test = redirectUri + "bb82338c_0ffe6666_f4f419fd&option=null";

        try {
            final String s = NonCommand.getCode(test);
            Assert.assertEquals("bb82338c_0ffe6666_f4f419fd", s);

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void test_6() {
        final String test = redirectUri + "bb8233&8c_0ffe6666_f4f419fd";

        try {
            final String s = NonCommand.getCode(test);
            Assert.assertEquals("bb8233", s);

        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void test_7() {
        final String test = redirectUri + "bb@82338c_0ffe6666_f4f4@19fd";

        try {
            final String s = NonCommand.getCode(test);
            Assert.assertEquals("bb", s);

        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }
}