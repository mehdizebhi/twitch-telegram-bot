package dev.mehdizebhi.twitchtelegrambot.config;

import dev.mehdizebhi.twitchtelegrambot.bot.TwinifyBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot-token}")
    private String botToken;

    @Bean
    public TelegramBotsLongPollingApplication telegramBotApplication(TwinifyBot twinifyBot) throws TelegramApiException {
        twinifyBot.onRegister();
        // Instantiate Telegram Bots API
        var botsApplication = new TelegramBotsLongPollingApplication();
        // Register your newly created AbilityBot
        botsApplication.registerBot(botToken, twinifyBot);
        return botsApplication;
    }
}
