package logic;

import commands.AuthCommand;
import commands.HelpCommand;
import commands.NonCommand;
import commands.StartCommand;
import inline_query_commands.GetCurrentPlayingObject;
import inline_query_commands.GetLikedSongsPlaylist;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;



public final class BotLogic extends TelegramLongPollingCommandBot {
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
        if (update.hasInlineQuery()) {
            var inlineQueryId = update.getInlineQuery().getId();
            Integer cacheTime;

            ArrayList<InlineQueryResult> inlineQueryResults = new ArrayList<>();

            if (update.getInlineQuery().getQuery().equals("nowplaying")) {
                inlineQueryResults.add(new GetCurrentPlayingObject().constructInlineQueryResult(update.getInlineQuery().getFrom(),
                        "Текущий воспроизводимый трек"));
                cacheTime = 3;
            } else if (update.getInlineQuery().getQuery().equals("likedsongs")) {
                inlineQueryResults.add(new GetLikedSongsPlaylist().constructInlineQueryResult(update.getInlineQuery().getFrom(),
                        "Ваши сохранённые треки"));
                cacheTime = 60;
            } else {
                InputTextMessageContent message = new InputTextMessageContent();
                message.setMessageText("Неправильная команда");
                inlineQueryResults.add(new InlineQueryResultArticle("Wrong Query", "Ожидание команды...", message));
                cacheTime = 3;
            }
            // Добавлять объекты с результатами ниже

            AnswerInlineQuery answerInlineQuery = AnswerInlineQuery.builder()
                    .inlineQueryId(inlineQueryId)
                    .cacheTime(cacheTime)
                    .results(inlineQueryResults)
                    .isPersonal(true)
                    .build();
            try {
                this.execute(answerInlineQuery);
            } catch (TelegramApiException e) {
                System.out.println("TelegramApiException");
                e.printStackTrace();
            }
        }
        else {
            // Если update не имеет inlineQuery, значит, пользователь отправил код для авторизации
            Message msg = update.getMessage();
            User user = update.getMessage().getFrom();
            Long chatId = msg.getChatId();

            String answer = nonCommandHandler.nonCommandExecute(user, msg.getText());
            setAnswer(chatId, answer);
        }
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

