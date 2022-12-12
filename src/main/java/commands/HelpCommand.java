package commands;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;


/**
 * Команда "Помощь"
 */
public class HelpCommand extends ServiceCommand {

    /**
     * Зарегистрировать команду помощи.
     *
     * @param identifier  уникальное название команды
     * @param description описание команды
     */
    public HelpCommand(final String identifier, final String description) {
        super(identifier, description);
    }

    /**
     * Сформировать обращение к пользователю.
     * Никнейм первичен. Если ник не установлен, обращаемся по имени и фамилии.
     */
    private String getUserAppeal(final User user) {
        final String appeal = user.getUserName();

        if (appeal == null) {
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        }

        return appeal;
    }

    /**
     * Обработчик команды помощи.
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        sendAnswer(absSender, chat.getId(),
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
                        @spotify\\_now\\_bot likedsongs - создание плейлиста с вашими любимыми треками
                        @spotify\\_now\\_bot nowplaying - ссылка на текущий воспроизводимый трек
                        """);
    }
}
