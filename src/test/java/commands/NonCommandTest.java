package commands;

import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.User;

public class NonCommandTest {

    @Test
    public void checkWhenTextNull() {
        NonCommand nonCommand = new NonCommand();
        User user = new User(843L, "testFirstName", false);
        String expectedAnswer = "Простите, я не понимаю Вас. Похоже, что Вы ввели сообщение, " +
                "не соответствующее формату. Возможно, Вам поможет /help";

        Assert.assertEquals(expectedAnswer, nonCommand.nonCommandExecute(user, null));
    }
}