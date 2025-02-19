package by.fleamarket.utilis;

import by.fleamarket.config.ApplicationContextProvider;
import by.fleamarket.exceptions.SendRequestExecution;
import by.fleamarket.model.TelegramBot;
import by.fleamarket.utilis.adUtils.AdPhotoUtils;
import by.fleamarket.utilis.adUtils.AdUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j
public class BotExecutorUtils {

    private TelegramBot bot;
    private final MessageUtils messageUtils;

    public BotExecutorUtils(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }


    public void executeResponse(String text, long chatId) {
        bot = ApplicationContextProvider.getApplicationContext().getBean(TelegramBot.class);
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new SendRequestExecution(e);
        }
    }

    public void executeResponse(SendMessage message, long chatId) {
        bot = ApplicationContextProvider.getApplicationContext().getBean(TelegramBot.class);
        message.setChatId(chatId);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new SendRequestExecution(e);
        }
    }

    public void executeAdResponseWithoutPhotos(AdUtils adUtils, long chatId) {
        SendMessage sendMessage = new SendMessage();
        bot = ApplicationContextProvider.getApplicationContext().getBean(TelegramBot.class);
        sendMessage.setChatId(chatId);
        sendMessage = messageUtils.generateAdConfirmSendMessage
                (chatId, adUtils.getTitle(), adUtils.getCategory(),
                        adUtils.getDescription()
                        , adUtils.getLocation(), adUtils.getPrice(), "-");
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new SendRequestExecution(e);
        }
    }

    public void executeAdResponseWithPhotos(AdUtils adUtils, long chatId) {
        SendMessage sendMessage = new SendMessage();
        bot = ApplicationContextProvider.getApplicationContext().getBean(TelegramBot.class);
        sendMessage.setChatId(chatId);
        sendMessage = messageUtils.generateAdConfirmSendMessage
                (chatId, adUtils.getTitle(), adUtils.getCategory(),
                        adUtils.getDescription()
                        , adUtils.getLocation(), adUtils.getPrice());
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new SendRequestExecution(e);
        }
    }

    public void executeMediaGroupResponse(List<AdPhotoUtils> adPhotos, long chatId) {
        bot = ApplicationContextProvider.getApplicationContext().getBean(TelegramBot.class);
        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setChatId(chatId);
        List<InputMedia> mediaList = new ArrayList<>();
        String[] fileNamesArray = new String[adPhotos.size()];
        InputMedia media;

        for (int i = 0; i < adPhotos.size(); i++) {
            media = new InputMediaPhoto();
            String fileName = "photo_" + (i + 1);
            media.setMedia(writeContentInFile(adPhotos.get(i).getBinaryContent().getFileAsArrayOfBytes(), fileName), fileName);
            fileNamesArray[i] = fileName;
            mediaList.add(media);
        }

        mediaGroup.setMedias(mediaList);
        try {
            bot.execute(mediaGroup);
        } catch (TelegramApiException e) {
            throw new SendRequestExecution(e);
        }

        deleteUsedFiles(fileNamesArray);

    }

    public void executePhotoResponse(AdPhotoUtils adPhotoUtils, long chatId) {
        bot = ApplicationContextProvider.getApplicationContext().getBean(TelegramBot.class);
        SendPhoto photo = new SendPhoto();
        String fileName = "photo_1";

        photo.setPhoto(new InputFile(writeContentInFile(adPhotoUtils.getBinaryContent().getFileAsArrayOfBytes(), fileName)));
        photo.setChatId(chatId);
        try {
            String baseUrl = bot.getBaseUrl();
            bot.execute(photo);
        } catch (TelegramApiException e) {
            throw new SendRequestExecution(e);
        }

        deleteUsedFiles(fileName);
    }

    private File writeContentInFile(byte[] binaryContent, String fileName) {
        File file = new File(fileName);
        try {
            Files.write(file.toPath(), binaryContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    private void deleteUsedFiles(String[] fileNames) {
        File file;
        for (int i = 0; i < fileNames.length; i++) {
            file = new File(fileNames[i]);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void deleteUsedFiles(String fileName) {
        File file;
        file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
