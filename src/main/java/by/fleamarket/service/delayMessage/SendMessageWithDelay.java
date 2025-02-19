package by.fleamarket.service.delayMessage;

import by.fleamarket.cash.AdCash;
import by.fleamarket.cash.BotStateCash;
import by.fleamarket.service.keyboard.InlineKeyboardService;
import by.fleamarket.utilis.BotExecutorUtils;
import by.fleamarket.utilis.MessageUtils;
import by.fleamarket.utilis.adUtils.AdUtils;
import by.fleamarket.utilis.enums.BotState;
import lombok.extern.log4j.Log4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


@Log4j
public class SendMessageWithDelay extends Thread {

    private final BotExecutorUtils botExecutor;
    private final AdCash adCash;
    private final MessageUtils messageUtils;
    private final InlineKeyboardService inlineKeyboardService;
    private long chatId;
    private long userId;
    private BotState state;
    private BotStateCash botStateCash;
    private boolean isRunning = false;

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public SendMessageWithDelay(BotExecutorUtils botExecutor, AdCash adCash, MessageUtils messageUtils, InlineKeyboardService inlineKeyboardService, BotState state, BotStateCash botStateCash) {
        this.botExecutor = botExecutor;
        this.adCash = adCash;
        this.messageUtils = messageUtils;
        this.inlineKeyboardService = inlineKeyboardService;
        this.state = state;
        this.botStateCash = botStateCash;
    }


    public void run() {
        try {
            Thread.sleep(10000);
            if (!Thread.currentThread().isInterrupted()) {
                botExecutor.executeResponse("Обработка почти закончена...\uD83E\uDDA6", chatId);
            }
            Thread.sleep(5000);

        } catch (InterruptedException e) {

        }
        if (!Thread.currentThread().isInterrupted()) {
            AdUtils preparingAd = adCash.getPreparingAd(userId);
            botStateCash.saveBotState(userId, BotState.POST_AD_CONFIRM);

            SendMessage sendMessage = messageUtils.generateAdConfirmSendMessage
                    (chatId, preparingAd.getTitle(), preparingAd.getCategory(),
                            preparingAd.getDescription()
                            , preparingAd.getLocation(), preparingAd.getPrice());

            botExecutor.executeResponse(sendMessage, chatId);
            botExecutor.executeMediaGroupResponse(preparingAd.getPhotos(), chatId);

            sendMessage = inlineKeyboardService.generateConfirmInlineKeyboard(chatId, state.getNumberOfState() + 1, "'Фото'");
            botExecutor.executeResponse(sendMessage, chatId);

            log.debug("Delay message was sent");

        }
    }

}
