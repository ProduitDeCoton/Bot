package logic;

import commands.AuthCommand;
import commands.HelpCommand;
import commands.NonCommand;
import commands.StartCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public final class BotLogic extends TelegramLongPollingCommandBot{
    private final String BOT_NAME;
    private final String BOT_TOKEN;
    private final NonCommand nonCommandHandler;


    public BotLogic(String botName, String botToken) {
        super();
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        this.nonCommandHandler = new NonCommand();
        register(new StartCommand("start", "Старт"));
        register(new HelpCommand("help","Помощь"));
        register(new AuthCommand("auth", "Авторизация в Spotify"));
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    /**
     * Ответ на запрос, не являющийся командой
     */
    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg = update.getMessage();
        User user = msg.getFrom();
        Long chatId = msg.getChatId();

        String answer = nonCommandHandler.nonCommandExecute(user, msg.getText());
        setAnswer(chatId, answer);
    }

    /**
     * Отправка ответа
     * @param chatId id чата
     * @param text текст ответа
     */
    private void setAnswer(Long chatId, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            System.out.println("TelegramApiException");
            e.printStackTrace();
        }
    }

}

