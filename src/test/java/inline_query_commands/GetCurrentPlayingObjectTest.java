package inline_query_commands;

import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

public class GetCurrentPlayingObjectTest {
    @Test
    public void tryToCheckWhenSessionNull() {
        User user = new User(865L, "testFirstName", false);
        var command = new GetCurrentPlayingObject();
        InlineQueryResultArticle result = (InlineQueryResultArticle) command.constructInlineQueryResult(user, "test");
        InputTextMessageContent resultMessage = (InputTextMessageContent) result.getInputMessageContent();
        var resultString = resultMessage.getMessageText();

        Assert.assertEquals(resultString, "Сейчас ничего не играет в Spotify");
    }

}