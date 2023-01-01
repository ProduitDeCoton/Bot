package logic;

import commands.*;
import inline_query_commands.GetCurrentPlayingObject;
import inline_query_commands.GetLikedSongsPlaylist;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import resources.CommonAnswers;
import spotify.exceptions.SpotifyActionFailedException;

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
        register(new GroupCommand("group", "Создание групповой сессии"));
        register(new AddCommand("add", "Добавить трек в очередь"));
        register(new SkipCommand("skip", "Пропуск текущего трека"));
        register(new CloseGroupSessionCommand("close", "Закрытие групповой сессии"));
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
        if (update.hasInlineQuery())
            processInlineQueryUpdate(update);

        else if (update.hasCallbackQuery()) {
            processCreatingGroup(update);
        }

        else if (update.getMessage().isUserMessage()) {
            // Если зашли в этот if, значит, пользователь отправил код для авторизации
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    public void processInlineQueryUpdate(Update update) {
        var inlineQueryId = update.getInlineQuery().getId();
        int cacheTime;

        ArrayList<InlineQueryResult> inlineQueryResults = new ArrayList<>();

        switch (update.getInlineQuery().getQuery()) {
            case "nowplaying" -> {
                inlineQueryResults.add(new GetCurrentPlayingObject().constructInlineQueryResult(update.getInlineQuery().getFrom(),
                        "Текущий воспроизводимый трек"));
                cacheTime = 3;
            }
            case "likedsongs" -> {
                inlineQueryResults.add(new GetLikedSongsPlaylist().constructInlineQueryResult(update.getInlineQuery().getFrom(),
                        "Ваши сохранённые треки"));
                cacheTime = 60;
            }
            default -> {
                InputTextMessageContent message = new InputTextMessageContent();
                message.setMessageText("""
                        Неправильная команда

                        Для просмотра доступных команд используйте /help в чате со мной""");
                inlineQueryResults.add(new InlineQueryResultArticle("Wrong Query", "Ожидание команды...", message));
                cacheTime = 3;
            }
        }

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

    public void processCreatingGroup(Update update) {
        User leader = update.getCallbackQuery().getFrom();
        Chat chat = update.getCallbackQuery().getMessage().getChat();

        ActiveGroups.createGroup(leader, chat);

        try {
            ActiveGroups.getGroupSession(chat).transferPlayback(update.getCallbackQuery().getData());
            ActiveUsers.getSession(leader).getSpotifyApi().skipToNextTrack(null);
        } catch (SpotifyActionFailedException e) {
            ActiveGroups.closeGroupSession(chat);
            setAnswer(update.getCallbackQuery().getMessage().getChatId(), CommonAnswers.GROUP_LEADER_PREMIUM_EXPIRED);
            return;
        }

        setAnswer(update.getCallbackQuery().getMessage().getChatId(),
                "Устройство " + update.getCallbackQuery().getData() + " выбрано. " +
                        "Приятного прослушивания!" + "\n\n" +
                        "/add - добавить трек в очередь\n" +
                        "/skip - пропустить трек");
    }

}

