package commands;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;


/**
 * Команда "Помощь"
 */
public class HelpCommand extends ServiceCommand {

    public HelpCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Я музыкальный бот, для работы со мной вы должны пройти аутентификацию\n" +
                        "\n" +
                        "Для этого нажми /auth\n" +
                        "\n" +
                        "Я могу показать текущий трек, играюший на платформе 'spotify'\n" +
                        "\n" +
                        "и т.д.\n" +
                        "\n" +
                        "❗Список команд\n" +
                        "/auth - аутентификация в 'spotify'\n" +
                        "/help - помощь\n");
    }
}
