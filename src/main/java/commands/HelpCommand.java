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
                """
                        Я музыкальный бот, для работы со мной вы должны пройти аутентификацию

                        Для этого нажмите /auth

                        Я могу показать текущий трек, играющий на платформе Spotify
                        Создать плейлист из сохранённых песен
                        И работать в групповом режиме, для этого добавьте меня в вашу беседу
                        ВАЖНО: для корректной работы группового режима требуется Spotify Premium.

                        ❗Список команд
                        /auth - аутентификация в Spotify
                        /help - помощь

                        /group - создание групповой сессии
                        /add (трек) - добавление трека в очередь

                        Команды, вызываемые в любом чате:
                        @spotify_now_bot likedsongs - создание плейлиста с вашими любимыми треками
                        @spotify_now_bot nowplaying - ссылка на текущий воспроизводимый трек""");
    }
}
