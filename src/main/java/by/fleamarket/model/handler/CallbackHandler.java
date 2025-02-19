package by.fleamarket.model.handler;

import by.fleamarket.model.handler.events.CallbackEvents;
import by.fleamarket.model.handler.events.MessageEvents;
import by.fleamarket.utilis.enums.BotState;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@AllArgsConstructor
@Log4j
public class CallbackHandler {

    private final CallbackEvents callbackEvents;
    private final MessageEvents messageEvents;

    public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery, BotState state) {
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        if (callbackQuery.getFrom().getUserName() == null) {
            return messageEvents.sendMessage(chatId, "Вы не можете пользовать ботом," +
                    " пока не заполните поле 'User Name' в настройках телеграм");
        }


        String[] processedData = processDataFromCallbackQuery(data);
        String key = processedData[0];
        String value = processedData[1];

        if (key.equals("category") && state.name().equals("POST_AD_CATEGORIES")) {
            return callbackEvents.postAdCategory(userId, chatId, key, value);
        }

        if (key.equals("back") && state.getNumberOfState() == Integer.parseInt(value)) {
            return callbackEvents.processBackCommand(userId, chatId, Integer.parseInt(value));
        }

        if (key.equals("blank") && state.getNumberOfState() == Integer.parseInt(value)) {
            return callbackEvents.processBlankCommand(userId, chatId, Integer.parseInt(value));
        }

        if (key.equals("negotiated") && state.getNumberOfState() == Integer.parseInt(value)) {
            return callbackEvents.processNegotiatedCommand(userId, chatId);
        }

        if (key.equals("terminate") && state.getNumberOfState() == Integer.parseInt(value)) {
            return callbackEvents.processTerminateCommand(userId, chatId);
        }

        if (key.equals("confirm") && state.getNumberOfState() == Integer.parseInt(value)) {
            return callbackEvents.processConfirmCommand(userId, chatId);
        }

        if (key.equals("edit") && state.getNumberOfState() == Integer.parseInt(value)) {
            return null;
        }

        log.debug("Was received callback not connected to state, user: " + userId);

        return null;
    }

    private String[] processDataFromCallbackQuery(String data) {
        String[] processedData = data.split(":");
        return processedData;
    }
}
