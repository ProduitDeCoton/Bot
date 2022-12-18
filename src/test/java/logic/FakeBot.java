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

// TODO: подготовить FakeBot для написания тестов AddCommand, GroupCommand, SkipCommand
public class FakeBot extends TelegramLongPollingCommandBot {
    private final String BOT_NAME = "fake_bot";
    private final String BOT_TOKEN = "88005553535";
    private final NonCommand nonCommandHandler;

    private static SendMessage lastAnswer;

    FakeBot() {
        this.nonCommandHandler = new NonCommand();
        register(new StartCommand("start", "Старт"));
        register(new HelpCommand("help","Помощь"));
        register(new AuthCommand("auth", "Авторизация в Spotify"));
    }

    public static SendMessage getLastAnswer() {
        return lastAnswer;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg = update.getMessage();
        User user = update.getMessage().getFrom();

        String answer = nonCommandHandler.nonCommandExecute(user, msg.getText());
        lastAnswer = new SendMessage();
        lastAnswer.setText(answer);
    }
}
