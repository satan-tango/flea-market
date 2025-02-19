package by.fleamarket.model.handler;

import by.fleamarket.dao.UserDAO;
import by.fleamarket.cash.BotStateCash;
import by.fleamarket.model.handler.events.MessageEvents;
import by.fleamarket.service.keyboard.MenuService;
import by.fleamarket.utilis.enums.BotState;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@AllArgsConstructor
@Log4j
public class MessageHandler {

    private final UserDAO userDAO;
    private final MessageEvents messageEvents;
    private final BotStateCash botStateCash;
    private final MenuService menuService;

    public BotApiMethod<?> handleInputMessage(Message message, BotState botState) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        if (!userDAO.isExistUser(userId)) {
            if (!messageEvents.isFilledUserName(message)) {
                return messageEvents.sendMessage(chatId, "Вы не можете пользовать ботом," +
                        " пока не заполните поле 'User Name' в настройках телеграм");
            }

            messageEvents.saveNewUser(message, userId);
            log.debug("User with id: " + userId + " was added to database");
        }
        if (!messageEvents.isFilledUserName(message)) {
            return messageEvents.sendMessage(chatId, "Вы не можете пользовать ботом," +
                    " пока не заполните поле 'User Name' в настройках телеграм");
        }

        botStateCash.saveBotState(userId, botState);

        List<BotState> states = botStateCash.getListOfBotStates(userId);

        switch (botState.name()) {
            case "NEW_USER" -> {
                botStateCash.cleaningBotStateCash(userId);
                return menuService.generateMainMenu(userId, "Привет\uD83D\uDC50, \n" +
                        " Это бот по подачи объявления на фли маркет");
            }
            case "START" -> {
                botStateCash.cleaningBotStateCash(userId);
                return menuService.generateMainMenu(userId);
            }
            case "POST_AD" -> {
                return messageEvents.postAd(userId);
            }
            case "POST_AD_TITLE" -> {
                return messageEvents.postAdTitle(userId, message);
            }
            case "POST_AD_DESCRIPTION" -> {
                return messageEvents.postAdDescription(userId, message);
            }
            case "POST_AD_LOCATION" -> {
                return messageEvents.postAdLocation(userId, message);
            }
            case "POST_AD_PRICE" -> {
                return messageEvents.postAdPrice(userId, message);
            }
            case "POST_AD_PHOTOS" -> {
                return messageEvents.postAdPhotos(userId, message);
            }
            case "MY_AD" -> {
                return messageEvents.showMyAds(chatId, userId);
            }
            default -> {
                return null;
            }
        }

    }
}
