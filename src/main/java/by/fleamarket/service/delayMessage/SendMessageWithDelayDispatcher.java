package by.fleamarket.service.delayMessage;

import by.fleamarket.cash.AdCash;
import by.fleamarket.cash.BotStateCash;
import by.fleamarket.service.keyboard.InlineKeyboardService;
import by.fleamarket.utilis.BotExecutorUtils;
import by.fleamarket.utilis.MessageUtils;
import by.fleamarket.utilis.enums.BotState;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Service
@Log4j
public class SendMessageWithDelayDispatcher {
    private final BotExecutorUtils botExecutor;
    private final AdCash adCash;
    private final MessageUtils messageUtils;
    private final InlineKeyboardService inlineKeyboardService;
    private final BotStateCash botStateCash;
    SendMessageWithDelay sendMessageWithDelay;

    public SendMessageWithDelayDispatcher(BotExecutorUtils botExecutor, AdCash adCash, MessageUtils messageUtils, InlineKeyboardService inlineKeyboardService, BotStateCash botStateCash) {
        this.botExecutor = botExecutor;
        this.adCash = adCash;
        this.messageUtils = messageUtils;
        this.inlineKeyboardService = inlineKeyboardService;
        this.botStateCash = botStateCash;
    }

    public boolean isThreadRunning() {
        if (sendMessageWithDelay != null) {
            if (sendMessageWithDelay.isAlive()) {
                return true;
            } else {
                return false;
            }
        } else {
            log.debug("Thread doesn't exist");
            return false;
        }
    }

    public void shutdownThread() {
        sendMessageWithDelay.setRunning(false);
        sendMessageWithDelay.interrupt();
    }

    public void startThread(long chatId, long userId, BotState state) {
        sendMessageWithDelay = new SendMessageWithDelay(botExecutor, adCash, messageUtils, inlineKeyboardService, state, botStateCash);
        sendMessageWithDelay.setRunning(true);
        sendMessageWithDelay.setChatId(chatId);
        sendMessageWithDelay.setUserId(userId);
        sendMessageWithDelay.start();
    }
}
