package by.fleamarket.model.handler.events;

import by.fleamarket.cash.AdCash;
import by.fleamarket.cash.BotStateCash;
import by.fleamarket.dao.AdDAO;
import by.fleamarket.dao.UserDAO;
import by.fleamarket.entity.Ad;
import by.fleamarket.entity.User;
import by.fleamarket.service.FileService;
import by.fleamarket.service.delayMessage.SendMessageWithDelayDispatcher;
import by.fleamarket.service.keyboard.InlineKeyboardService;
import by.fleamarket.service.ValidationInputDataService;
import by.fleamarket.utilis.*;
import by.fleamarket.utilis.adUtils.AdUtils;
import by.fleamarket.utilis.enums.BotState;
import by.fleamarket.utilis.enums.ValidationState;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Log4j
public class MessageEvents {

    private final UserDAO userDAO;
    private final MessageUtils messageUtils;
    private final AdCash adCash;
    private final BotStateCash botStateCash;
    private final ValidationInputDataService validateData;
    private final InlineKeyboardService inlineKeyboardService;
    private final FileService fileService;
    private final BotExecutorUtils botExecutor;
    private final SendMessageWithDelayDispatcher sendMessageWithDelayDispatcher;
    private final AdDAO adDAO;
    private final MappingUtils mappingUtils;


    public void saveNewUser(Message message, Long userId) {
        User user = User.builder()
                .telegramUserId(userId)
                .firstName(message.getFrom().getFirstName())
                .lastName((message.getFrom().getLastName()))
                .username(message.getFrom().getUserName())
                .build();

        userDAO.saveNewUser(user);
    }

    public boolean isFilledUserName(Message message) {
        if (message.getFrom().getUserName() != null) {
            return true;
        } else {
            return false;
        }
    }

    public BotApiMethod<?> sendMessage(long chatId, String message) {
        return messageUtils.generateSendMessage(chatId, message);
    }


    public BotApiMethod<?> postAd(Long userId) {
        botStateCash.cleaningBotStateCash(userId);
        SendMessage sendMessage = messageUtils.generateSendMessage(userId, "Введите название товара✏️");
        botStateCash.saveBotState(userId, BotState.POST_AD_TITLE);

        return sendMessage;
    }

    public BotApiMethod<?> postAdTitle(long userId, Message message) {
        long chatId = message.getChatId();
        SendMessage sendMessage;
        BotState state = botStateCash.getCurrentBotState(userId);

        String title = message.getText().trim();
        ValidationState validationState = validateData.validateAdTitle(title);

        if (validationState == ValidationState.INVALID_LENGTH) {
            log.debug("Invalid length of title was received: '" + title + "'" + " from user: " + userId);
            return messageUtils.generateSendMessage(userId, "Невереная длинна название. Попробуй снова.");
        }

        if (validationState == ValidationState.INVALID_CONTENT) {
            log.debug("Invalid title was received: '" + title + "'" + " from user: " + userId);
            return messageUtils.generateSendMessage(userId, "Использованы недопустимые символы," +
                    " вы можете использовть кириллицу и латиницу, а также цифры. Попробуйте снова.");
        }


        AdUtils ad = AdUtils.builder()
                .title(title)
                .build();
        adCash.savePreparingAd(userId, ad);
        botStateCash.saveBotState(userId, BotState.POST_AD_CATEGORIES);
        log.debug("Title '" + title + "' was saved to preparing ad, user: " + userId);

        sendMessage = messageUtils.generateAdSendMessage(userId, title);
        sendMessage.setReplyMarkup(inlineKeyboardService.generateInlineKeyboardForCategories());
        botExecutor.executeResponse(sendMessage, chatId);

        sendMessage = inlineKeyboardService.generateBackInlineKeyboard(chatId, "'Название'", state.getNumberOfState() + 1);

        return sendMessage;
    }


    public BotApiMethod<?> postAdDescription(long userId, Message message) {
        long chatId = message.getChatId();
        AdUtils ad = adCash.getPreparingAd(userId);
        SendMessage sendMessage;
        BotState state = botStateCash.getCurrentBotState(userId);

        String description = message.getText().trim();
        if (ad == null) {
            botStateCash.cleaningBotStateCash(userId);
            log.debug("Preparing ad was not found after sending description, user: " + userId + "message: " + message);
            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так, попробуй сначала");
        }


        ValidationState validationState = validateData.validateAdDescription(description);

        if (validationState == ValidationState.INVALID_CONTENT) {
            log.debug("Invalid description was received: '" + description + "'" + " from user: " + userId);
            return messageUtils.generateSendMessage(userId, "Использованы недопустимые символы," +
                    " вы можете использовть кириллицу и латиницу," +
                    " а также цифры и следующие символы: ',', '.', '-'");
        }

        if (validationState == ValidationState.INVALID_LENGTH) {
            log.debug("Invalid length of description was received: '" + description + "'" + " from user: " + userId);
            return messageUtils.generateSendMessage(userId, "Невереная длинна, попробуйте еще раз");
        }

        if (validationState == ValidationState.SKIP_CONTENT) {
            ad.setDescription("-");
            log.debug("Description: '-' was saved to preparing ad, user: " + userId);
        } else {
            ad.setDescription(description);
            log.debug("Description : '" + description + "'" + "was saved to preparing ad, user: " + userId);
        }

        adCash.savePreparingAd(userId, ad);
        botStateCash.saveBotState(userId, BotState.POST_AD_LOCATION);
        sendMessage = messageUtils.generateAdSendMessage(chatId, ad.getTitle(), ad.getCategory(), ad.getDescription());
        botExecutor.executeResponse(sendMessage, chatId);


        sendMessage = inlineKeyboardService.generateBackAndBlankFieldInlineKeyboard(chatId, "'Описание'", state.getNumberOfState() + 1);

        return sendMessage;
    }

    public BotApiMethod<?> postAdLocation(long userId, Message message) {
        long chatId = message.getChatId();
        SendMessage sendMessage;
        BotState state = botStateCash.getCurrentBotState(userId);

        AdUtils ad = adCash.getPreparingAd(userId);
        if (ad == null) {
            botStateCash.cleaningBotStateCash(userId);
            log.debug("Preparing ad was not found after sending location, user: " + userId + "message: " + message);
            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так. Попробуйте сначала.");
        }

        String location = message.getText().trim();
        log.debug("Description was received: '" + location + "'" + " from user: " + userId);


        ValidationState validationState = validateData.validateAdLocation(location);

        if (validationState == ValidationState.INVALID_CONTENT) {
            log.debug("Invalid location was received: '" + location + "'" + " from user: " + userId);
            return messageUtils.generateSendMessage(userId, "Использованы недопустимые символы," +
                    " вы можете использовть кириллицу и латиницу," +
                    " а также цифры и следующие символы: ',', '.', '-'. Попробуйте снова.");
        }

        if (validationState == ValidationState.INVALID_LENGTH) {
            log.debug("Invalid length of location was received: '" + location + "'" + " from user: " + userId);
            return messageUtils.generateSendMessage(userId, "Невереная длинна. Попробуйте снова.");
        }

        if (validationState == ValidationState.SKIP_CONTENT) {
            ad.setLocation("-");
            log.debug("Location: '-' was saved, user: " + userId);
        } else {
            ad.setLocation(location);
            log.debug("Location : '" + location + "'" + "was saved, user: " + userId);
        }


        adCash.savePreparingAd(userId, ad);

        botStateCash.saveBotState(userId, BotState.POST_AD_PRICE);

        sendMessage = messageUtils.generateAdSendMessage(chatId, ad.getTitle(), ad.getCategory(),
                ad.getDescription(), ad.getLocation());
        botExecutor.executeResponse(sendMessage, chatId);


        sendMessage = inlineKeyboardService.generateBackAndNegotiatedPriceInlineKeyboard(chatId, "'Локация'", state.getNumberOfState() + 1);

        return sendMessage;

    }

    public BotApiMethod<?> postAdPrice(long userId, Message message) {
        long chatId = message.getChatId();
        SendMessage sendMessage;
        BotState state = botStateCash.getCurrentBotState(userId);

        AdUtils ad = adCash.getPreparingAd(userId);
        if (ad == null) {
            botStateCash.cleaningBotStateCash(userId);
            log.debug("Preparing ad was not found after sending price, user: " + userId + " message: " + message);

            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так. Попробуйте сначала.");
        }

        String price = message.getText().trim();

        ValidationState validationState = validateData.validateAdPrice(price);

        if (validationState == ValidationState.INVALID_CONTENT) {
            log.debug("Invalid price was received: '" + price + "'" + " from user: " + userId);

            return messageUtils.generateSendMessage(userId, "Использованы недопустимые символы," +
                    " вы можете использовть только цифры и название валюты в трех больших латинских буквах." +
                    " Доступные валюты: BYN, USD, RUB, EUR (Пример: 120 BYN, 10 USD). Попробуйте снова.");
        }

        if (validationState == ValidationState.INVALID_LENGTH) {
            log.debug("Invalid length of location was received: '" + price + "'" + " from user: " + userId);

            return messageUtils.generateSendMessage(userId, "Невереная длинна. Попробуйте снова.");
        }

        if (validationState == ValidationState.AGREED_PRICE) {
            ad.setPrice("Договорная");
            adCash.savePreparingAd(userId, ad);
            log.debug("Price: 'договорная' was saved to preparing ad, user: " + userId);
            botStateCash.saveBotState(userId, BotState.POST_AD_PHOTOS);

            sendMessage = messageUtils.generateAdSendMessage(chatId, ad.getTitle(), ad.getCategory(),
                    ad.getDescription(), ad.getLocation(), ad.getPrice());
            botExecutor.executeResponse(sendMessage, chatId);


            sendMessage = inlineKeyboardService.generateBackAndBlankFieldInlineKeyboard(chatId, "'Цена'", state.getNumberOfState() + 1);

            return sendMessage;
        }

        if (validationState == ValidationState.THERE_IS_NO_CURRENCY) {
            log.debug("Price this no currency was received: '" + price + "'" + " from user: " + userId);

            return messageUtils.generateSendMessage(userId, "Валюта не обнаружена. " +
                    "Доступные валюты: BYN, USD, RUB, EUR (Пример: 120 BYN, 10 USD). Попробуйте снова.");
        }

        if (validationState == ValidationState.UNAVAILABLE_CURRENCY) {
            log.debug("Price this unavailable currency was received: '" + price + "'" + " from user: " + userId);

            return messageUtils.generateSendMessage(userId, "Вы использовали недопутимую валюту. " +
                    "Доступные валюты: BYN, USD, RUB, EUR (Пример: 120 BYN, 10 USD). Попробуйте снова.");
        }

        ad.setPrice(price);
        adCash.savePreparingAd(userId, ad);
        log.debug("Price: '" + price + "' was saved, user: " + userId);
        botStateCash.saveBotState(userId, BotState.POST_AD_PHOTOS);


        sendMessage = messageUtils.generateAdSendMessage(chatId, ad.getTitle(), ad.getCategory(),
                ad.getDescription(), ad.getLocation(), ad.getPrice());
        botExecutor.executeResponse(sendMessage, chatId);


        sendMessage = inlineKeyboardService.generateBackAndBlankFieldInlineKeyboard(chatId, "'Цена'", state.getNumberOfState() + 1);

        return sendMessage;
    }


    // ! Нельзя вызвать у списка метод, если он не объявлян(null)
    public BotApiMethod<?> postAdPhotos(long userId, Message message) {
        long chatId = message.getChatId();
        SendMessage sendMessage;
        BotState state = botStateCash.getCurrentBotState(userId);
        AdUtils preparingAd = adCash.getPreparingAd(userId);


        if (preparingAd == null) {
            botStateCash.cleaningBotStateCash(userId);
            log.debug("Preparing ad was not found after sending photo, user: " + userId + " message: " + message);

            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так. Попробуйте сначала.");
        }

        if (message.hasText() && !message.hasPhoto()) {
            if (validateData.isSkipping(message.getText().trim())) {
                log.debug("Photos: 'пропуск' was saved, user: " + userId);
                preparingAd.setPhotos(null);
                adCash.savePreparingAd(userId, preparingAd);
                botStateCash.saveBotState(userId, BotState.POST_AD_CONFIRM);

                sendMessage = messageUtils.generateAdConfirmSendMessage
                        (chatId, preparingAd.getTitle(), preparingAd.getCategory(),
                                preparingAd.getDescription()
                                , preparingAd.getLocation(), preparingAd.getPrice(), "-");
                botExecutor.executeResponse(sendMessage, chatId);

                sendMessage = inlineKeyboardService.generateConfirmInlineKeyboard(chatId, state.getNumberOfState() + 1, "'Фото'");

                return sendMessage;

            }
        }
        if (message.hasPhoto()) {
            if (message.getMediaGroupId() == null) {
                preparingAd = fileService.processPhoto(message, preparingAd, userId);
                adCash.savePreparingAd(userId, preparingAd);
                botStateCash.saveBotState(userId, BotState.POST_AD_CONFIRM);
                sendMessage = messageUtils.generateAdConfirmSendMessage
                        (chatId, preparingAd.getTitle(), preparingAd.getCategory(),
                                preparingAd.getDescription()
                                , preparingAd.getLocation(), preparingAd.getPrice());

                botExecutor.executeResponse(sendMessage, chatId);
                botExecutor.executePhotoResponse(preparingAd.getPhotos().get(0), chatId);

                sendMessage = inlineKeyboardService.generateConfirmInlineKeyboard(chatId, state.getNumberOfState() + 1, "'Фото'");
            } else {
                if (preparingAd.getPhotos() == null) {
                    preparingAd = fileService.processPhoto(message, preparingAd, userId);
                    adCash.savePreparingAd(userId, preparingAd);

                    sendMessage = messageUtils.generateSendMessage(chatId, "Идет обработка фотографий, пожалуйста, подождите" +
                            ", это может занять до 20 секунд...\uD83D\uDE43");
                } else {
                    if (preparingAd.getPhotos().size() == 3) {
                        sendMessage = messageUtils.generateSendMessage(chatId, "Вы не можете поместить больше трех фотографий в" +
                                " объявления\uD83D\uDCDB. Первые три фотографии обрабатываются... Это займет некоторое время\uD83E\uDD25");
                    } else {
                        preparingAd = fileService.processPhoto(message, preparingAd, userId);
                        adCash.savePreparingAd(userId, preparingAd);
                        if (!sendMessageWithDelayDispatcher.isThreadRunning()) {
                            sendMessageWithDelayDispatcher.startThread(chatId, userId, state);
                        }
                        if (preparingAd.getPhotos().size() == 2) {
                            sendMessage = messageUtils.generateSendMessage(chatId, "Второе фото обрабатывется... Подождите еще чуть-чуть\uD83E\uDD13");
                        } else {
                            sendMessage = messageUtils.generateSendMessage(chatId, "Третье фото в процессе обработки...\uD83E\uDDA7");
                        }
                    }
                }
            }
            return sendMessage;

        }

        return null;
    }


    public BotApiMethod<?> showMyAds(long chatId, long userId) {
        SendMessage sendMessage;
        User user = userDAO.findByTelegramUserId(userId);

        List<Ad> listOfAds = adDAO.findByUser(user);
        //      userDAO.deleteUser(user);
        adDAO.deleteAd(listOfAds.get(0));

        if (listOfAds == null) {
            sendMessage = messageUtils.generateSendMessage(chatId, "У вас пока нет объявлений");

            return sendMessage;
        }

        List<AdUtils> adUtilsList = new ArrayList<>();
       /* for (int i = 0; i < listOfAds.size(); i++) {
            adUtilsList.add(mappingUtils.mapToAdUtils(listOfAds.get(i)));
        }
        for (int i = 0; i < adUtilsList.size(); i++) {
            if (adUtilsList.get(i).getPhotos() == null) {
                botExecutor.executeAdResponseWithoutPhotos(adUtilsList.get(i), chatId);
            } else {
                botExecutor.executeAdResponseWithPhotos(adUtilsList.get(i), chatId);

                if (adUtilsList.get(i).getPhotos().size() > 1) {
                    botExecutor.executeMediaGroupResponse(adUtilsList.get(i).getPhotos(), chatId);
                } else {
                    botExecutor.executePhotoResponse(adUtilsList.get(i).getPhotos().get(0), chatId);
                }
            }
        }
*/
        return null;
    }
}
