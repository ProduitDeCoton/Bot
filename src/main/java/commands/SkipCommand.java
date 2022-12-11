package commands;

import logic.ActiveGroups;
import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import spotify.exceptions.SpotifyActionFailedException;

public class SkipCommand extends ServiceCommand {

    public SkipCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        if (ActiveGroups.getGroupSession(chat) == null) {
            sendAnswer(absSender, chat.getId(),
                    """
                            Групповая музыкальная сессия в этом чате не создана.

                            Запустите групповую сессию при помощи команды /group""");
            return;
        }

        var leader = ActiveGroups.getGroupSession(chat).getLeader();

        if (ActiveUsers.getSession(leader).getTokenExpiresIn() < 30) {
            ActiveUsers.getSession(leader).authorizeByRefreshToken();
        }

        var track = ActiveUsers.getSession(user).getSpotifyApi().getCurrentlyPlayedObject(null).getItem();
        var artists = ActiveUsers.getSession(user).getSpotifyApi().getTrack(track.getId(), null).getArtists();
        String trackName = track.getName();
        String artistName = artists.get(0).getName();

        try {
            ActiveUsers.getSession(user).getSpotifyApi().skipToNextTrack(null);
        } catch (SpotifyActionFailedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            String messageText;
            if (chat.getType().equals("private")) {
                messageText = """
                        Похоже, у вас отсутствует подписка Spotify Premium.

                        Продлите срок действия подписки и попробуйте ещё раз.""";
            } else {
                messageText = """
                        Похоже, у лидера отсутствует подписка Spotify Premium. Групповая сессия закрыта.

                        Попробуйте создать группу с другим лидером, у которого оплачена подписка.""";
            }

            ActiveGroups.closeGroupSession(chat);
            sendAnswer(absSender, chat.getId(), messageText);
        }

        sendAnswer(absSender, chat.getId(), "*" + trackName + " - " + artistName +
                "* пропущена.");
    }
}
