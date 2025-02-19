package by.fleamarket.utilis.keyboard;

import by.fleamarket.utilis.enums.CategoriesUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class InlineKeyboardUtils {
    InlineKeyboardMarkup inlineKeyboardMarkup;

    public InlineKeyboardMarkup getInlineKeyboardForCategories() {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttons;

        buttons = new ArrayList<>();
        int counter = 1;
        int temp = 0;

        for (CategoriesUtils category : CategoriesUtils.values()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(category.getCategoryName());
            button.setCallbackData("category:" + String.valueOf(category.getValue()));

            buttons.add(button);
            listOfButtons.add(temp, buttons);

            if (counter == 2) {
                listOfButtons.remove(temp + 1);
                counter = 0;
                temp++;
                buttons = new ArrayList<>();
            }
            counter++;

        }

        inlineKeyboardMarkup.setKeyboard(listOfButtons);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineKeyboardForConfirmPreparingAd(int numberOfState, String field) {

        inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttons;

        buttons = new ArrayList<>();
        InlineKeyboardButton confirmButton = new InlineKeyboardButton();
        confirmButton.setText("✅Отправить объявление");
        confirmButton.setCallbackData("confirm:" + numberOfState);
        buttons.add(confirmButton);
        listOfButtons.add(buttons);

        buttons = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("⬅️Вернуться к полю " + field);
        backButton.setCallbackData("back:" + numberOfState);
        buttons.add(backButton);
        listOfButtons.add(buttons);


        buttons = new ArrayList<>();
        InlineKeyboardButton editButton = new InlineKeyboardButton();
        editButton.setText("\uD83D\uDEE0Редактировать");
        editButton.setCallbackData("edit:" + numberOfState);
        buttons.add(editButton);
        listOfButtons.add(buttons);

        buttons = new ArrayList<>();
        InlineKeyboardButton terminateButton = new InlineKeyboardButton();
        terminateButton.setText("❌Прервать подачу объявления");
        terminateButton.setCallbackData("terminate:" + numberOfState);
        buttons.add(terminateButton);
        listOfButtons.add(buttons);


        inlineKeyboardMarkup.setKeyboard(listOfButtons);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineKeyboardForBackToPreviousField(String field, int numberOfState) {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttons;


        buttons = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("⬅️Вернуться к полю " + field);
        backButton.setCallbackData("back:" + numberOfState);
        buttons.add(backButton);
        listOfButtons.add(buttons);

        buttons = new ArrayList<>();
        InlineKeyboardButton terminateButton = new InlineKeyboardButton();
        terminateButton.setText("❌Прервать подачу объявления");
        terminateButton.setCallbackData("terminate:" + numberOfState);
        buttons.add(terminateButton);
        listOfButtons.add(buttons);


        inlineKeyboardMarkup.setKeyboard(listOfButtons);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineKeyboardForBackToPreviousFieldAndToBlandField(String field, int numberOfState) {
        inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttons;

        buttons = new ArrayList<>();
        InlineKeyboardButton blankField = new InlineKeyboardButton();
        blankField.setText("\uD83D\uDDD2Пустое поле");
        blankField.setCallbackData("blank:" + numberOfState);
        buttons.add(blankField);
        listOfButtons.add(buttons);

        buttons = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("⬅️Вернуться к полю " + field);
        backButton.setCallbackData("back:" + numberOfState);
        buttons.add(backButton);
        listOfButtons.add(buttons);

        buttons = new ArrayList<>();
        InlineKeyboardButton terminateButton = new InlineKeyboardButton();
        terminateButton.setText("❌Прервать подачу объявления");
        terminateButton.setCallbackData("terminate:" + numberOfState);
        buttons.add(terminateButton);
        listOfButtons.add(buttons);


        inlineKeyboardMarkup.setKeyboard(listOfButtons);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineKeyboardForBackToPreviousFieldAndNegotiatedPrice(String field, int numberOfState) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttons;

        buttons = new ArrayList<>();
        InlineKeyboardButton negotiatedPriceField = new InlineKeyboardButton();
        negotiatedPriceField.setText("\uD83E\uDD1DДоговорная цена");
        negotiatedPriceField.setCallbackData("negotiated:" + numberOfState);
        buttons.add(negotiatedPriceField);
        listOfButtons.add(buttons);

        buttons = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("⬅️Вернуться к полю " + field);
        backButton.setCallbackData("back:" + numberOfState);
        buttons.add(backButton);
        listOfButtons.add(buttons);

        buttons = new ArrayList<>();
        InlineKeyboardButton terminateButton = new InlineKeyboardButton();
        terminateButton.setText("❌Прервать подачу объявления");
        terminateButton.setCallbackData("terminate:" + numberOfState);
        buttons.add(terminateButton);
        listOfButtons.add(buttons);


        inlineKeyboardMarkup.setKeyboard(listOfButtons);

        return inlineKeyboardMarkup;
    }
}
