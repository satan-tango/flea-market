package by.fleamarket.model;

import by.fleamarket.cash.BotStateCash;
import by.fleamarket.model.handler.CallbackHandler;
import by.fleamarket.model.handler.MessageHandler;
import by.fleamarket.utilis.enums.BotState;
import by.fleamarket.utilis.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@AllArgsConstructor
@Log4j
public class TelegramFacade {

    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;
    private final MessageUtils messageUtils;
    private final BotStateCash botStateCash;

    public BotApiMethod<?> handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            long userId = callbackQuery.getFrom().getId();
            BotState state = botStateCash.getCurrentBotState(userId);

            if (state == null || state == BotState.START) {
                log.debug("Was received callback not connected to state, user: " + userId);
                return null;
            } else {
                return callbackHandler.handleCallbackQuery(callbackQuery, state);
            }
        } else {
            Message message = update.getMessage();
            long userId = message.getFrom().getId();
            BotState state = botStateCash.getCurrentBotState(userId);


            if (message.hasPhoto() && state == BotState.POST_AD_PHOTOS) {
                return messageHandler.handleInputMessage(message, state);
            }

            if (message.hasText()) {
                if (state == BotState.POST_AD_TITLE) {
                    return messageHandler.handleInputMessage(message, state);
                }

                if (state == BotState.POST_AD_DESCRIPTION) {
                    return messageHandler.handleInputMessage(message, state);
                }

                if (state == BotState.POST_AD_LOCATION) {
                    return messageHandler.handleInputMessage(message, state);
                }

                if (state == BotState.POST_AD_PRICE) {
                    return messageHandler.handleInputMessage(message, state);
                }

                if (state == BotState.POST_AD_PHOTOS) {
                    return messageHandler.handleInputMessage(message, state);
                }

                return handleInputMessage(message);
            } else {
                SendMessage sendMessage = messageUtils.generateSendMessage(update.getMessage().getFrom().getId(),
                        "Unsupported message");
                log.debug("Unsupported message \n"
                        + "First Name: " + update.getMessage().getFrom().getFirstName() +
                        "; User Id: " + update.getMessage().getFrom().getId());
                return sendMessage;
            }
        }
    }

    private BotApiMethod<?> handleInputMessage(Message message) {
        BotState botState;
        String inputMessage = message.getText();
        long userId = message.getFrom().getId();


        switch (inputMessage) {
            case "/start":
                botState = BotState.NEW_USER;
                break;
            case "\uD83D\uDDDEПодать объявление":
                botState = BotState.POST_AD;
                break;
            case "\uD83D\uDCC2Мои объявление":
                botState = BotState.MY_AD;
                break;
            case "\uD83D\uDD0DНайти объявление":
                botState = BotState.FIND_AD;
                break;
            case "⚙\uFE0FНастройки":
                botState = BotState.SETTINGS;
                break;
            case "\uD83D\uDEBCПоддержка":
                botState = BotState.SUPPORT;
                break;
            default:
                botState = botStateCash.getCurrentBotState(userId) == null
                        ? BotState.START : botStateCash.getCurrentBotState(userId);

        }
        return messageHandler.handleInputMessage(message, botState);
    }
}
