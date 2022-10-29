package inline_query_commands;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;

public abstract class InlineQueryCommand {
    protected InputTextMessageContent buildAnswerMessage(String answerText) {
        return InputTextMessageContent.builder()
                .messageText(answerText)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    public abstract InlineQueryResult constructInlineQueryResult(User user, String showableInlineQueryText);
}
