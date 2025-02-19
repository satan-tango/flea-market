package by.fleamarket.service.keyboard;

import by.fleamarket.utilis.keyboard.InlineKeyboardUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
@AllArgsConstructor
public class InlineKeyboardService {

    private final InlineKeyboardUtils inlineKeyboardUtils;


    public InlineKeyboardMarkup generateInlineKeyboardForCategories() {
        InlineKeyboardMarkup keyboardMarkup = inlineKeyboardUtils.getInlineKeyboardForCategories();

        return keyboardMarkup;
    }

    public SendMessage generateConfirmInlineKeyboard(long chatId, int numberOfState, String field) {
        InlineKeyboardMarkup keyboardMarkup = inlineKeyboardUtils.getInlineKeyboardForConfirmPreparingAd(numberOfState, field);
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText("Выберите операцию:");
        message.setReplyMarkup(keyboardMarkup);

        return message;

    }

    public SendMessage generateBackInlineKeyboard(long chatId, String field, int numberOfState) {
        InlineKeyboardMarkup keyboardMarkup = inlineKeyboardUtils.getInlineKeyboardForBackToPreviousField(field, numberOfState);
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText("Выберите операцию:");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    public SendMessage generateBackAndBlankFieldInlineKeyboard(long chatId, String field, int numberOfState) {
        InlineKeyboardMarkup keyboardMarkup = inlineKeyboardUtils.getInlineKeyboardForBackToPreviousFieldAndToBlandField(field, numberOfState);
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText("Выберите операцию:");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    public SendMessage generateBackAndNegotiatedPriceInlineKeyboard(long chatId, String field, int numberOfState) {
        InlineKeyboardMarkup keyboardMarkup = inlineKeyboardUtils.getInlineKeyboardForBackToPreviousFieldAndNegotiatedPrice(field, numberOfState);
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText("Выберите операцию:");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }
}
