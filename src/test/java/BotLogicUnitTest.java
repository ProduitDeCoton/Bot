import org.junit.Assert;
import org.junit.Test;
import res.BotMessages;

public class BotLogicUnitTest {
    Console testLogic = new Console();

    @Test
    public void question1() {
        Assert.assertNotEquals("Question 1 must not be empty string",
                "", BotMessages.question1);
        Assert.assertNotEquals("Question 1 must not be null",
                null, BotMessages.question1);
    }

    @Test
    public void question2() {
        Assert.assertNotEquals("Question 2 must not be empty string",
                "", BotMessages.question2);
        Assert.assertNotEquals("Question 2 must not be null",
                null, BotMessages.question2);
    }

    @Test
    public void question3() {
        Assert.assertNotEquals("Question 3 must not be empty string",
                "", BotMessages.question3);
        Assert.assertNotEquals("Question 3 must not be null",
                null, BotMessages.question3);
    }

}
