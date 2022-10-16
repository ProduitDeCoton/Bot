package telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBot extends TelegramLongPollingBot {
    private static TelegramBot instance = null;
    private TelegramBotLogic botLogic = new TelegramBotLogic();

    @Override
    public String getBotUsername() {
        return System.getenv("TELEGRAM_BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("TELEGRAM_BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        botLogic.setUpdate(update);
        String userMessage = botLogic.getUserMessage();
        botLogic.sendAnswer(userMessage);
    }

    public static TelegramBot getInstance() {
        if (instance == null)
            instance = new TelegramBot();
        return instance;
    }
}
