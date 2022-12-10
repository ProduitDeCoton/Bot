package commands;

import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработка сообщений с командой создания групповой сессии.
 */
public class GroupCommand extends ServiceCommand {

    public GroupCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());

        if (chat.getType().equals("private")) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                    new StringBuilder()
                            .append("Похоже, вы пытаетесь создать групповую сессию в личном чате.")
                            .append("\n\n")
                            .append("Создайте групповую сессию в чате со своими друзьями.").toString());
            return;
        }

        if (ActiveUsers.getSession(user) == null) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                    new StringBuilder()
                            .append("Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.")
                            .append("\n\n")
                            .append("Для этого введите в чат со мной команду /auth").toString());
            return;
        }

        if (ActiveUsers.getSession(user).getTokenExpiresIn() <= 30) {
            ActiveUsers.getSession(user).authorizeByRefreshToken();
        }

        var devices = ActiveUsers.getSession(user).getSpotifyApi().getAvailableDevices().getDevices();

        if (devices.size() == 0) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,

                    new StringBuilder()
                            .append("Spotify не запущен ни на одном устройстве. ")
                            .append("Пожалуйста, запустите приложение и повторите попытку.").toString());
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
