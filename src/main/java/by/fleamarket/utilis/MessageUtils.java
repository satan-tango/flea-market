package by.fleamarket.utilis;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


@Component
public class MessageUtils {


    public SendMessage generateSendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        return sendMessage;
    }

    public SendMessage generateSendMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));

        return sendMessage;
    }

    public SendMessage generateAdSendMessage(long chatId, String title) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("\uD83D\uDCD2НАЗВАНИЕ: " + title + "\n\n\nВыберите категорию\uD83D\uDD0D");

        return sendMessage;
    }

    public SendMessage generateAdSendMessage(long chatId, String title, String category) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("\uD83D\uDCD2НАЗВАНИЕ:  " + title + "\n\n\uD83D\uDD0DКАТЕГОРИЯ:  "
                + category + "\n\n\nОтправьте описание товара ✏\uFE0F, если хотите ничего не указавыть," +
                " то выполните одно из следующих условий:" +
                "\n▫\uFE0FОтпрвьте символ '-'" +
                "\n▫\uFE0FОтправьте слово 'пропуск'" +
                "\n▫\uFE0FНажмите кнопку в меню операций 'Пустое поле'");

        return sendMessage;
    }

    public SendMessage generateAdSendMessage(long chatId, String title, String category,
                                             String description) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("\uD83D\uDCD2НАЗВАНИЕ:  " + title + "\n\n\uD83D\uDD0DКАТЕГОРИЯ:  "
                + category + "\n\n\uD83D\uDCDDОПИСАНИЕ:  " + description + "\n\n\nОтправьте вашу локацию ✏\uFE0F" +
                " (Пример: Минск, Октябрьский район), если хотите ничего не указавыть," +
                " то выполните одно из следующих условий:" +
                "\n▫\uFE0FОтпрвьте символ '-'" +
                "\n▫\uFE0FОтправьте слово 'пропуск'" +
                "\n▫\uFE0FНажмите кнопку в меню операций 'Пустое поле'");

        return sendMessage;
    }

    public SendMessage generateAdSendMessage(long chatId, String title, String category,
                                             String description, String location) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("\uD83D\uDCD2НАЗВАНИЕ:  " + title + "\n\n\uD83D\uDD0DКАТЕГОРИЯ:  "
                + category + "\n\n\uD83D\uDCDDОПИСАНИЕ:  " + description + "\n\n\uD83D\uDDFAЛОКАЦИЯ:  " + location +
                "\n\n\nОтправьте желаемую цену✏\uFE0F" +
                " (не забудьте указать валюту. Список доступных вылют : BYN, RUB, USD, EUR. Пример: 100USD)." +
                "\nТакже предусмотерн вариант 'договорная'. Для этого выполните одно из следующих условий:" +
                "\n▫\uFE0FОтпрвьте слова 'договорная'" +
                "\n▫\uFE0FНажмите кнопку в меню операций 'Договорная цена'");

        return sendMessage;
    }

    public SendMessage generateAdSendMessage(long chatId, String title, String category,
                                             String description, String location, String price) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("\uD83D\uDCD2НАЗВАНИЕ:  " + title + "\n\n\uD83D\uDD0DКАТЕГОРИЯ:  "
                + category + "\n\n\uD83D\uDCDDОПИСАНИЕ:  " + description + "\n\n\uD83D\uDDFAЛОКАЦИЯ:  " + location +
                "\n\n\uD83D\uDCB0ЦЕНА:  " + price +
                "\n\n\nОтправьте фото товара(не больше трех фотографий)\uD83D\uDCE8." +
                " Доступен варинт без фото, для этого выполните одно из следующих условий:" +
                "\n▫\uFE0FОтпрвьте символ '-'" +
                "\n▫\uFE0FОтправьте слово 'пропуск'" +
                "\n▫\uFE0FНажмите кнопку в меню операций 'Пустое поле'");

        return sendMessage;
    }

    public SendMessage generateAdConfirmSendMessage(long chatId, String title, String category,
                                                    String description, String location, String price, String photos) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("\uD83D\uDCD2НАЗВАНИЕ:  " + title + "\n\n\uD83D\uDD0DКАТЕГОРИЯ:  "
                + category + "\n\n\uD83D\uDCDDОПИСАНИЕ:  " + description + "\n\n\uD83D\uDDFAЛОКАЦИЯ:  " + location +
                "\n\n\uD83D\uDCB0ЦЕНА:  " + price +
                "\n\n\uD83C\uDF8EФОТО:  " + photos +
                "\n\n\nПроверьте введеные данные, если все соответсвует, то нажмите:\n▫\uFE0F✅Отправить объявление" +
                "\nЕсли хотите вернуться к полю 'Фото', то нажмите:\n ▫\uFE0F⬅️Вернуться к полю 'Фото'" +
                "\nЕсли хотите редактировать какое-то друго поле, то нажмите:\n ▫\uFE0F\uD83D\uDEE0Редактировать" +
                "\nЕсли хотите прекратить подачу объявления, то нажмите:\n ▫\uFE0F❌Прервать подачу объявления");

        return sendMessage;
    }

    public SendMessage generateAdConfirmSendMessage(long chatId, String title, String category,
                                                    String description, String location, String price) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("\uD83D\uDCD2НАЗВАНИЕ:  " + title + "\n\n\uD83D\uDD0DКАТЕГОРИЯ:  "
                + category + "\n\n\uD83D\uDCDDОПИСАНИЕ:  " + description + "\n\n\uD83D\uDDFAЛОКАЦИЯ:  " + location +
                "\n\n\uD83D\uDCB0ЦЕНА:  " + price +
                "\n\n\nПроверьте введеные данные, если все соответсвует, то нажмите:\n▫\uFE0F✅Отправить объявление" +
                "\nЕсли хотите вернуться к полю 'Фото', то нажмите:\n ▫\uFE0F⬅️Вернуться к полю 'Фото'" +
                "\nЕсли хотите редактировать какое-то друго поле, то нажмите:\n ▫\uFE0F\uD83D\uDEE0Редактировать" +
                "\nЕсли хотите прекратить подачу объявления, то нажмите:\n ▫\uFE0F❌Прервать подачу объявления");

        return sendMessage;

    }

}
