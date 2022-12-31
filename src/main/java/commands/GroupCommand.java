package commands;

import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import resources.CommonAnswers;

import java.util.ArrayList;
import java.util.List;

public class GroupCommand extends ServiceCommand {

    public GroupCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        if (chat.getType().equals("private")) {
            sendAnswer(absSender, chat.getId(),
                    """
                            Похоже, вы пытаетесь создать групповую сессию в личном чате.

                            Создайте групповую сессию в чате со своими друзьями.""");
            return;
        }

        if (ActiveUsers.getSession(user) == null) {
            sendAnswer(absSender, chat.getId(), CommonAnswers.USER_NOT_AUTHORISED);
            return;
        }

        if (ActiveUsers.getSession(user).getTokenExpiresIn() <= 30) {
            ActiveUsers.getSession(user).authorizeByRefreshToken();
        }


        var devices = ActiveUsers.getSession(user).getSpotifyApi().getAvailableDevices().getDevices();

        if (devices.size() == 0) {
            sendAnswer(absSender, chat.getId(),
                    "Spotify не запущен ни на одном устройстве. Пожалуйста, запустите " +
                            "приложение и повторите попытку");
            return;
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        for (var device : devices) {
            InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
            keyboardButton.setText(device.getName());
            keyboardButton.setCallbackData(device.getName());

            keyboardRows.add(List.of(keyboardButton));
        }

        keyboardMarkup.setKeyboard(keyboardRows);

        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());
        message.setText("Выберите устройство для воспроизведения:");
        message.setReplyMarkup(keyboardMarkup);
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
