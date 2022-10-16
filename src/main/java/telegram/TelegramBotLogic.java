package telegram;
import logic.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Objects;

public class TelegramBotLogic extends BotLogic {
    private static final TelegramLongPollingBot sender = TelegramBot.getInstance();
    private static Update update;

    public void setUpdate(Update newUpdate) {
        update = newUpdate;
    }

    @Override
    public void say(String message) {
        SendMessage response = new SendMessage();
        response.setChatId(update.getMessage().getChatId().toString());
        response.setText(message);

        try {
            sender.execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getUserMessage() {
        return update.getMessage().getText();
    }

    @Override
    public String waitMessage() {
        String oldMessage = update.getMessage().getText();
        while (true) {
            if (!Objects.equals(update.getMessage().getText(), oldMessage))
                return update.getMessage().getText();
        }
    }
}
