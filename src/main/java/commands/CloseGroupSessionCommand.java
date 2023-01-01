package commands;

import logic.ActiveGroups;
import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import resources.CommonAnswers;

public class CloseGroupSessionCommand extends ServiceCommand{
    public CloseGroupSessionCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        if (ActiveGroups.getGroupSession(chat) == null) {
            sendAnswer(absSender, chat.getId(), CommonAnswers.GROUP_NOT_CREATED);
            return;
        }

        if (chat.getType().equals("private")) {
            sendAnswer(absSender, chat.getId(), """
                    Похоже, вы пытаетесь закрыть групповую сессию в приватном чате

                    Пожалуйста, используйте это команду в группе или супергруппе""");
        }

        ActiveUsers.getSession(ActiveGroups.getGroupSession(chat).getLeader())
                        .getSpotifyApi().pausePlayback(null);
        ActiveGroups.closeGroupSession(chat);

        sendAnswer(absSender, chat.getId(), "Групповая сессия закрыта. До встречи на новой вечеринке!");
    }
}
