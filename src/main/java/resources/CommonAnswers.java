package resources;

/**
 * Класс для часто повторяющихся ответов бота.
 * Содержит в себе строковые константы с ответами.
 */
public final class CommonAnswers {
    public static final String USER_NOT_AUTHORISED =
            """
             Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.
    
             Для этого введите в чат со мной команду /auth
             """;

    public static final String GROUP_NOT_CREATED =
            """
            Групповая музыкальная сессия в этом чате не создана.

            Запустите групповую сессию при помощи команды /group
            """;

    public static final String GROUP_LEADER_PREMIUM_EXPIRED =
            """
            Похоже, у лидера отсутствует подписка Spotify Premium. Групповая сессия закрыта.

            Попробуйте создать группу с другим лидером, у которого оплачена подписка.
            """;

    public static final String USER_PREMIUM_EXPIRED =
            """
            Похоже, у вас отсутствует подписка Spotify Premium.

            Продлите срок действия подписки и попробуйте ещё раз.
            """;
}
