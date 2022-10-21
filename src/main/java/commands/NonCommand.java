package commands;

import exceptions.WrongAuthRedirectUriException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class NonCommand {
    public static String getCode(String authRedirectUri) throws WrongAuthRedirectUriException {
        Pattern pattern = Pattern.compile("http://localhost:8080/auth/spotify/redirect\\?code=([-_A-Za-z0-9]+)");
        Matcher matcher = pattern.matcher(authRedirectUri);

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new WrongAuthRedirectUriException();
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

