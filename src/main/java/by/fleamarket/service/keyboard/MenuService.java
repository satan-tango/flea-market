package by.fleamarket.service.keyboard;

import by.fleamarket.utilis.keyboard.MenuKeyboardUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Service
@AllArgsConstructor
public class MenuService {

    private final MenuKeyboardUtils menuKeyboardUtils;

    public SendMessage generateMainMenu(long userId) {
        ReplyKeyboardMarkup menuKeyboard = menuKeyboardUtils.getMainMenuKeyboard();
        SendMessage message = new SendMessage();

        message.setChatId(String.valueOf(userId));
        message.setText("Меню\uD83D\uDCBB");
        message.setReplyMarkup(menuKeyboard);

        return message;
    }

    public SendMessage generateMainMenu(long userId, String text) {
        ReplyKeyboardMarkup menuKeyboard = menuKeyboardUtils.getMainMenuKeyboard();
        SendMessage message = new SendMessage();

        message.setChatId(String.valueOf(userId));
        message.setText(text);
        message.setReplyMarkup(menuKeyboard);

        return message;
    }



}
