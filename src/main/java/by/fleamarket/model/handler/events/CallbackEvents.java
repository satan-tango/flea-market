package by.fleamarket.model.handler.events;

import by.fleamarket.cash.AdCash;
import by.fleamarket.cash.BotStateCash;
import by.fleamarket.dao.AdDAO;
import by.fleamarket.dao.AdPhotoDAO;
import by.fleamarket.dao.BinaryContentDAO;
import by.fleamarket.dao.UserDAO;
import by.fleamarket.entity.Ad;
import by.fleamarket.entity.AdPhoto;
import by.fleamarket.entity.User;
import by.fleamarket.service.keyboard.InlineKeyboardService;
import by.fleamarket.service.keyboard.MenuService;
import by.fleamarket.utilis.BotExecutorUtils;
import by.fleamarket.utilis.MappingUtils;
import by.fleamarket.utilis.MessageUtils;
import by.fleamarket.utilis.adUtils.AdUtils;
import by.fleamarket.utilis.enums.BotState;
import by.fleamarket.utilis.enums.CategoriesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Log4j
public class CallbackEvents {

    private final AdCash adCash;
    private final BotStateCash botStateCash;
    private final MessageUtils messageUtils;
    private final BotExecutorUtils botExecutor;
    private final InlineKeyboardService inlineKeyboardService;
    private final MappingUtils mappingUtils;
    private final AdDAO adDAO;
    private final UserDAO userDAO;
    private final AdPhotoDAO adPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final MenuService menuService;


    public BotApiMethod<?> postAdCategory(long userId, long chatId, String key, String value) {
        SendMessage sendMessage;
        BotState state = botStateCash.getCurrentBotState(userId);

        //Если не находи пользователя то возвращает null. Так работает метод get в HashMap
        AdUtils ad = adCash.getPreparingAd(userId);
        if (ad == null) {
            botStateCash.cleaningBotStateCash(userId);
            log.debug("Preparing ad was not found after choosing category, user:" + userId);
            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так. Попробуй сначала.");
        }

        Optional<CategoriesUtils> categoryOptional = Arrays.stream(CategoriesUtils.values())
                .filter(e -> e.getValue() == Integer.parseInt(value))
                .findFirst();
        CategoriesUtils category;
        if (categoryOptional.isPresent()) {
            category = categoryOptional.get();
        } else {
            log.debug("The required category was not found , user: " + userId);
            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так, попробуй снова");
        }


        ad.setCategory(category.getCategoryName());
        adCash.savePreparingAd(userId, ad);
        botStateCash.saveBotState(userId, BotState.POST_AD_DESCRIPTION);
        log.debug("Category '" + category.getCategoryName() + "' was saved to preparing ad, user: " + userId);

        sendMessage = messageUtils.generateAdSendMessage(userId, ad.getTitle(), ad.getCategory());
        botExecutor.executeResponse(sendMessage, chatId);


        sendMessage = inlineKeyboardService.generateBackAndBlankFieldInlineKeyboard(chatId, "'Категория'", state.getNumberOfState() + 1);

        return sendMessage;
    }

    public BotApiMethod<?> processBackCommand(long userId, long chatId, int numberOfState) {
        AdUtils preparingAd = adCash.getPreparingAd(userId);
        List<BotState> stateList = botStateCash.getListOfBotStates(userId);
        BotState state = botStateCash.getCurrentBotState(userId);
        SendMessage sendMessage;

        if (preparingAd == null) {
            botStateCash.cleaningBotStateCash(userId);
            log.debug("Preparing ad was not found after click 'back' button , user: " + userId);
            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так. Попробуй сначала.");
        }

        switch (numberOfState) {
            case 3: {
                log.debug("Title: '" + preparingAd.getTitle() + "' was removed from preparing ad, user: " + userId);
                preparingAd.setTitle(null);
                botStateCash.stepBackToOneState(userId);
                adCash.savePreparingAd(userId, preparingAd);

                sendMessage = messageUtils.generateSendMessage(userId, "Введите название товара✏️");

                return sendMessage;
            }
            case 4: {
                log.debug("Category: '" + preparingAd.getCategory() + "' was removed from preparing ad, user: " + userId);
                preparingAd.setCategory(null);
                adCash.savePreparingAd(userId, preparingAd);
                botStateCash.stepBackToOneState(userId);

                sendMessage = messageUtils.generateAdSendMessage(userId, preparingAd.getTitle());
                sendMessage.setReplyMarkup(inlineKeyboardService.generateInlineKeyboardForCategories());
                botExecutor.executeResponse(sendMessage, chatId);
                sendMessage = inlineKeyboardService.generateBackInlineKeyboard(chatId, "'Название'", state.getNumberOfState() - 1);

                return sendMessage;
            }
            case 5: {
                log.debug("Description: '" + preparingAd.getDescription() + "' was removed from preparing ad, user: " + userId);
                preparingAd.setDescription(null);
                adCash.savePreparingAd(userId, preparingAd);
                botStateCash.stepBackToOneState(userId);

                sendMessage = messageUtils.generateAdSendMessage(userId, preparingAd.getTitle(), preparingAd.getCategory());
                botExecutor.executeResponse(sendMessage, chatId);
                sendMessage = inlineKeyboardService.generateBackAndBlankFieldInlineKeyboard(chatId, "'Категория'", state.getNumberOfState() - 1);

                return sendMessage;
            }
            case 6: {
                log.debug("Location: '" + preparingAd.getLocation() + "' was removed from preparing ad, user: " + userId);
                preparingAd.setLocation(null);
                adCash.savePreparingAd(userId, preparingAd);
                botStateCash.stepBackToOneState(userId);

                sendMessage = messageUtils.generateAdSendMessage(chatId, preparingAd.getTitle(), preparingAd.getCategory(), preparingAd.getDescription());
                botExecutor.executeResponse(sendMessage, chatId);
                sendMessage = inlineKeyboardService.generateBackAndBlankFieldInlineKeyboard(chatId, "'Описание'", state.getNumberOfState() - 1);

                return sendMessage;
            }
            case 7: {
                log.debug("Price: '" + preparingAd.getPrice() + "' was removed from preparing ad, user: " + userId);
                preparingAd.setPrice(null);
                adCash.savePreparingAd(userId, preparingAd);
                botStateCash.stepBackToOneState(userId);

                sendMessage = messageUtils.generateAdSendMessage(chatId, preparingAd.getTitle(), preparingAd.getCategory(),
                        preparingAd.getDescription(), preparingAd.getLocation());
                botExecutor.executeResponse(sendMessage, chatId);
                sendMessage = inlineKeyboardService
                        .generateBackAndNegotiatedPriceInlineKeyboard(chatId, "'Локация'", state.getNumberOfState() - 1);

                return sendMessage;
            }
            case 8: {
                log.debug("Photos was removed from preparing ad, user: " + userId);
                preparingAd.setPhotos(null);
                adCash.savePreparingAd(userId, preparingAd);
                botStateCash.stepBackToOneState(userId);

                sendMessage = messageUtils.generateAdSendMessage(chatId, preparingAd.getTitle(), preparingAd.getCategory(),
                        preparingAd.getDescription(), preparingAd.getLocation(), preparingAd.getPrice());
                botExecutor.executeResponse(sendMessage, chatId);
                sendMessage = inlineKeyboardService.generateBackAndBlankFieldInlineKeyboard(chatId, "'Цена'", state.getNumberOfState() - 1);

                return sendMessage;
            }
            default: {
                return null;
            }
        }
    }

    public BotApiMethod<?> processBlankCommand(long userId, long chatId, int numberOfState) {
        AdUtils preparingAd = adCash.getPreparingAd(userId);
        List<BotState> stateList = botStateCash.getListOfBotStates(userId);
        BotState state = botStateCash.getCurrentBotState(userId);
        SendMessage sendMessage;

        if (preparingAd == null) {
            botStateCash.cleaningBotStateCash(userId);
            log.debug("Preparing ad was not found click after 'blank' button , user: " + userId);
            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так. Попробуй сначала.");
        }

        switch (numberOfState) {
            case 4: {
                log.debug("Description: 'пропуск' was saved to preparing ad, user: " + userId);
                preparingAd.setDescription("-");
                adCash.savePreparingAd(userId, preparingAd);
                botStateCash.saveBotState(userId, BotState.POST_AD_LOCATION);

                sendMessage = messageUtils.generateAdSendMessage(chatId, preparingAd.getTitle(), preparingAd.getCategory(), preparingAd.getDescription());
                botExecutor.executeResponse(sendMessage, chatId);
                sendMessage = inlineKeyboardService.generateBackAndBlankFieldInlineKeyboard(chatId, "'Описание'", state.getNumberOfState() + 1);

                return sendMessage;
            }
            case 5: {
                log.debug("Location: 'пропуск' was saved to preparing ad, user: " + userId);
                preparingAd.setLocation("-");
                adCash.savePreparingAd(userId, preparingAd);
                botStateCash.saveBotState(userId, BotState.POST_AD_PRICE);

                sendMessage = messageUtils.generateAdSendMessage(chatId, preparingAd.getTitle(), preparingAd.getCategory(),
                        preparingAd.getDescription(), preparingAd.getLocation());
                botExecutor.executeResponse(sendMessage, chatId);
                sendMessage = inlineKeyboardService.generateBackAndNegotiatedPriceInlineKeyboard(chatId, "'Локация'", state.getNumberOfState() + 1);

                return sendMessage;
            }
            case 7: {
                log.debug("Photos: 'пропуск' was saved to preparing ad, user: " + userId);
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
            default: {
                return null;
            }
        }
    }

    public BotApiMethod<?> processNegotiatedCommand(long userId, long chatId) {
        AdUtils preparingAd = adCash.getPreparingAd(userId);
        BotState state = botStateCash.getCurrentBotState(userId);
        SendMessage sendMessage;

        if (preparingAd == null) {
            botStateCash.cleaningBotStateCash(userId);
            log.debug("Preparing ad was not found click 'negotiated' button , user: " + userId);
            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так. Попробуй сначала.");
        }

        log.debug("Price: 'договорная' was saved to preparing ad, user: " + userId);
        preparingAd.setPrice("Договорная");
        adCash.savePreparingAd(userId, preparingAd);
        botStateCash.saveBotState(userId, BotState.POST_AD_PHOTOS);

        sendMessage = messageUtils.generateAdSendMessage(chatId, preparingAd.getTitle(), preparingAd.getCategory(),
                preparingAd.getDescription(), preparingAd.getLocation(), preparingAd.getPrice());
        botExecutor.executeResponse(sendMessage, chatId);
        sendMessage = inlineKeyboardService.generateBackAndBlankFieldInlineKeyboard(chatId, "'Цена'", state.getNumberOfState() + 1);

        return sendMessage;
    }

    //TODO Если какое-то поле не заполненео
    public BotApiMethod<?> processConfirmCommand(long userId, long chatId) {
        AdUtils preparingAd = adCash.getPreparingAd(userId);
        SendMessage sendMessage;

        if (preparingAd == null) {
            botStateCash.cleaningBotStateCash(userId);
            log.debug("Preparing ad was not found click 'confirm' button , user: " + userId);
            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так. Попробуй сначала.");
        }

        if (!isAllFieldsFilled(preparingAd)) {
            log.debug("One of the filed was not filled, user: " + userId);
            botStateCash.cleaningBotStateCash(userId);
            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так. Попробуй сначала.");
        }

        Ad ad = mappingUtils.mapToAd(preparingAd, userId);
        adDAO.saveNewAd(ad);

        User user = userDAO.findByTelegramUserId(userId);
        List<Ad> AdsList = adDAO.findByUser(user);

        sendMessage = menuService.generateMainMenu(userId, "Объявление успешно отправлено");
        botStateCash.cleaningBotStateCash(userId);

        return sendMessage;
    }

    private boolean isAllFieldsFilled(AdUtils preparingAd) {
        if (preparingAd.getTitle() == null) return false;
        if (preparingAd.getCategory() == null) return false;
        if (preparingAd.getDescription() == null) return false;
        if (preparingAd.getLocation() == null) return false;
        if (preparingAd.getPrice() == null) return false;
        return true;
    }

    public BotApiMethod<?> processTerminateCommand(long userId, long chatId) {
        AdUtils preparingAd = adCash.getPreparingAd(userId);
        SendMessage sendMessage;

        if (preparingAd == null) {
            botStateCash.cleaningBotStateCash(userId);
            log.debug("Preparing ad was not found click 'terminate' button , user: " + userId);
            return messageUtils.generateSendMessage(chatId, "Что-то пошло не так. Попробуй сначала.");
        }

        adCash.deletePreparingAd(userId);
        log.debug("Preparing ad was deleted, user: " + userId);

        sendMessage = menuService.generateMainMenu(userId, "Подача объявления прервана");
        botStateCash.cleaningBotStateCash(userId);

        return sendMessage;
    }
}
