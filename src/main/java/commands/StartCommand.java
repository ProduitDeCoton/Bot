package commands;


import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;


/**
 * Команда "Старт"
 */
public class StartCommand extends ServiceCommand {

    public StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        sendAnswer(absSender, chat.getId(),
                """
                        Привет, давай начнём! Для начала тебе необходимо пройти аутентификацию в Spotify. Для этого нажми /auth

                        Если тебе нужна помощь, нажми /help""");
    }
}
