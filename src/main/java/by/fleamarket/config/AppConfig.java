package by.fleamarket.config;

import by.fleamarket.botconfig.TelegramBotConfig;
import by.fleamarket.model.TelegramBot;
import by.fleamarket.model.TelegramFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
public class AppConfig {
    private final TelegramBotConfig botConfig;

    public AppConfig(TelegramBotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botConfig.getWebHookPath()).build();
    }

    @Bean
    public TelegramBot springWebHookBot(SetWebhook setWebhook, TelegramFacade telegramFacade) {
        TelegramBot telegramBot = new TelegramBot(telegramFacade, setWebhook);
        telegramBot.setBotPath(botConfig.getWebHookPath());
        telegramBot.setBotUsername(botConfig.getUsername());
        telegramBot.setBotToken(botConfig.getBotToken());

        return telegramBot;
    }
}
