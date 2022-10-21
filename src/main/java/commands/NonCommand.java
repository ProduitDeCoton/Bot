package commands;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class NonCommand {
    public static String getCode(String authRedirectUri)  {
        return null;
    }

    public String nonCommandExecute(Long chatId, String userName, String text) {
        String answer;
        String code;
        try {
            code = getCode(text);
            answer = "Всё правильно! Вы молодец";
        } catch (Exception e) {   //Сделать отдельное исключение
            answer = e.getMessage() +
                    "\n\n  Похоже вы неправильно ввели ссылку, попробуйте ещё раз";
        }

        catch (Exception e) {
            answer = "Простите, я не понимаю Вас. Похоже, что Вы ввели сообщение, не соответствующее формату. Возможно, Вам поможет /help";
        }
        return answer;
    }
}

