package com.example.botTurnos.bot;

import com.example.botTurnos.service.GroqService;
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
    private final GroqService groqService;

    public BarberBot(@Value("${telegram.bot.token}") String botToken,
                     @Value("${telegram.bot.username}") String botUsername,
                     GroqService groqService) {
        super(botToken);
        this.botUsername = botUsername;
        this.groqService = groqService;
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

            // 1. Enviar el mensaje del usuario a Groq
            String aiResponse = groqService.getGroqResponse(userMessage);

            // 2. Enviar la respuesta de Groq de vuelta a Telegram
            sendText(chatId, aiResponse);
        }
    }

    /**
     * Método helper para enviar un mensaje de texto.
     */
    private void sendText(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error al enviar mensaje: {}", e.getMessage());
        }
    }
}