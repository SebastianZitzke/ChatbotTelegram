package com.example.botTurnos.bot;

import com.example.botTurnos.service.GeminiService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class BarberBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(BarberBot.class);

    private final String botUsername;
    private final GeminiService geminiService;

    public BarberBot(@Value("${telegram.bot.token}") String botToken,
                     @Value("${telegram.bot.username}") String botUsername,
                     GeminiService geminiService) {
        super(botToken);
        this.botUsername = botUsername;
        this.geminiService = geminiService;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            log.info("Mensaje recibido de {}: {}", chatId, userMessage);

            String aiResponse = geminiService.getGeminiResponse(userMessage);
            sendText(chatId, aiResponse);
        }
    }

    private void sendText(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error al enviar mensaje: {}", e.getMessage());
        }
    }
}