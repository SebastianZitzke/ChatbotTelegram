package com.example.botTurnos.config;

import com.example.botTurnos.bot.BarberBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    // Este Bean crea la API de Telegram y registra tu bot.
    @Bean
    public TelegramBotsApi telegramBotsApi(BarberBot barberBot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(barberBot);
        return api;
    }
}