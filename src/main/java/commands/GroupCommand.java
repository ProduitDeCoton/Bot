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
 * Обработка сообщений, содержащих обращение к команде создания групповой сессии.
 */
public class GroupCommand extends ServiceCommand {

    /**
     * Зарегистрировать команду создания групповой сессии.
     *
     * @param identifier  уникальное название команды.
     * @param description описание команды.
     */
    public GroupCommand(String identifier, String description) {
        super(identifier, description);
    }

    /**
     * Сформировать обращение к пользователю.
     */
    private String getUserAppeal(final User user) {
        final String appeal = user.getUserName();

        if (appeal == null) {
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        }

        return appeal;
    }

    /**
     * Обработка команды создания группового прослушивания.
     */
    @Override
    public void execute(final AbsSender absSender, final User user, final Chat chat, final String[] args) {
        final String userAppeal = getUserAppeal(user);

        if (chat.getType().equals("private")) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userAppeal,
                    new StringBuilder()
                            .append("Похоже, вы пытаетесь создать групповую сессию в личном чате.")
                            .append("\n\n")
                            .append("Создайте групповую сессию в чате со своими друзьями.").toString());
            return;
        }

        if (ActiveUsers.getSession(user) == null) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userAppeal,
                    new StringBuilder()
                            .append("Пожалуйста, авторизуйтесь в Spotify в личных сообщениях со мной.")
                            .append("\n\n")
                            .append("Для этого введите в чат со мной команду /auth").toString());
            return;
        }

        if (ActiveUsers.getSession(user).getTokenExpiresIn() <= 30) {
            ActiveUsers.getSession(user).authorizeByRefreshToken();
        }

        final var devices = ActiveUsers.getSession(user).getSpotifyApi().getAvailableDevices().getDevices();

        if (devices.isEmpty()) {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userAppeal,
                    new StringBuilder()
                            .append("Spotify не запущен ни на одном устройстве. ")
                            .append("Пожалуйста, запустите приложение и повторите попытку.").toString());
            return;
        }

        final InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        final List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        for (final var device : devices) {
            final InlineKeyboardButton keyboardButton = new InlineKeyboardButton();

            keyboardButton.setText(device.getName());
            keyboardButton.setCallbackData(device.getName());

            keyboardRows.add(List.of(keyboardButton));
        }

        keyboardMarkup.setKeyboard(keyboardRows);

        final SendMessage message = new SendMessage();
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
