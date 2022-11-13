package logic;

import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotLogicTest {
    private final FakeBot fakeBot = new FakeBot();

    @Test
    public void nonCommandExecuteWithoutCode() {
        Update update = new Update();
        Message message = new Message();
        message.setText("testcase");
        update.setMessage(message);

        fakeBot.processNonCommandUpdate(update);
        Assert.assertEquals(FakeBot.getLastAnswer().getText(), "Похоже, вы неправильно ввели ссылку, попробуйте ещё раз");
    }

    @Test
    public void nonCommandExecuteWithNullText() {
        Update update = new Update();
        Message message = new Message();
        update.setMessage(message);

        fakeBot.processNonCommandUpdate(update);
        Assert.assertEquals(FakeBot.getLastAnswer().getText(), "Простите, я не понимаю Вас. Похоже, что Вы ввели сообщение, не соответствующее формату. Возможно, Вам поможет /help");
    }
}