package com.example.botTurnos.service;

import com.example.botTurnos.dto.GroqDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GroqService {

    private final WebClient webClient;
    private final String groqApiKey;

    public GroqService(WebClient.Builder webClientBuilder,
                       @Value("${groq.api.url}") String apiUrl,
                       @Value("${groq.api.key}") String groqApiKey) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.groqApiKey = groqApiKey;
    }

    /**
     * Envía un mensaje a la IA de Groq y espera una respuesta.
     */
    public String getGroqResponse(String userMessage) {

        // --- INGENIERÍA DE PROMPT (Formato OpenAI) ---
        // 1. Mensaje del Sistema (Define el rol de la IA)
        GroqDtos.Message systemMessage = new GroqDtos.Message(
                "system",
                """
                Eres "BarberBot", un asistente virtual para la barbería "El Leñador".
                Tu único objetivo es agendar turnos. NO hables de otros temas.
                Servicios: Corte ($5000, 30 min), Barba ($3000, 20 min), Corte y Barba ($7000, 50 min).
                Horarios: Lunes a Viernes (9:00-20:00), Sábados (9:00-14:00).
                Reglas: Sé amable, profesional y conciso.
                """
        );

        // 2. Mensaje del Usuario (Lo que preguntó)
        GroqDtos.Message userApiMessage = new GroqDtos.Message("user", userMessage);

        // --- Construcción del Request ---
        // Usamos un modelo rápido de Llama 3
        var requestBody = new GroqDtos.GroqRequest(
                "llama3-8b-8192",
                List.of(systemMessage, userApiMessage),
                0.7
        );

        try {
            // --- Llamada a la API de Groq ---
            GroqDtos.GroqResponse response = webClient.post()
                    .header("Authorization", "Bearer " + groqApiKey) // <-- MUY IMPORTANTE
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(GroqDtos.GroqResponse.class)
                    .block(); // .block() hace la llamada síncrona

            // --- Extracción de la respuesta ---
            if (response != null && !response.choices().isEmpty()) {
                // Devolvemos el contenido del mensaje de la primera "choice"
                return response.choices().get(0).message().content();
            }

            return "Lo siento, no pude procesar tu solicitud en este momento.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Hubo un error con la IA. Por favor, intenta de nuevo más tarde.";
        }
    }
}