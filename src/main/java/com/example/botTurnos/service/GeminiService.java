package com.example.botTurnos.service;

import com.example.botTurnos.dto.GeminiDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder,
                         @Value("${gemini.api.url}") String apiUrl,
                         @Value("${gemini.api.key}") String geminiApiKey) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.geminiApiKey = geminiApiKey;
    }

    /**
     * Envía un mensaje a la IA de Gemini y espera una respuesta.
     */
    public String getGeminiResponse(String userMessage) {

        // --- INGENIERÍA DE PROMPT ---
        // Aquí le damos el contexto y las reglas a Gemini.
        String fullPrompt = """
            Eres "BarberBot", un asistente virtual para la barbería "El Leñador".
            Tu único objetivo es agendar turnos. NO hables de otros temas.
            
            Servicios disponibles:
            - Corte: $5000 (30 min)
            - Barba: $3000 (20 min)
            - Corte y Barba: $7000 (50 min)
            
            Horarios de atención:
            - Lunes a Viernes: 9:00 a 20:00
            - Sábados: 9:00 a 14:00
            
            Reglas:
            1. Sé amable, profesional y conciso.
            2. Tu objetivo es obtener: [Servicio], [Día] y [Hora].
            3. Si el usuario te pregunta por otra cosa (fútbol, clima), responde amablemente que "solo estoy aquí para agendar turnos."
            4. Si obtienes todos los datos, finaliza con: "¡Perfecto! Tu turno para [Servicio] el [Día] a las [Hora] está pre-agendado. Te llegará la confirmación final en breve."
            
            Conversación actual:
            Usuario: "%s"
            Asistente:
            """.formatted(userMessage); // Inserta el mensaje del usuario en el prompt

        // --- Construcción del Request ---
        var part = new GeminiDtos.Part(fullPrompt);
        var content = new GeminiDtos.Content(List.of(part));
        var requestBody = new GeminiDtos.GeminiRequest(List.of(content));

        try {
            // --- Llamada a la API de Gemini ---
            GeminiDtos.GeminiResponse response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", geminiApiKey).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve() // Lanza error en 4xx/5xx
                    .bodyToMono(GeminiDtos.GeminiResponse.class)
                    .block(); // .block() hace la llamada síncrona (espera la respuesta)

            // --- Extracción de la respuesta ---
            if (response != null && !response.candidates().isEmpty()) {
                // Devolvemos el texto de la primera candidata
                return response.candidates().get(0).content().parts().get(0).text();
            }

            return "Lo siento, no pude procesar tu solicitud en este momento.";

        } catch (Exception e) {
            e.printStackTrace(); // Loguea el error
            return "Hubo un error con la IA. Por favor, intenta de nuevo más tarde.";
        }
    }
}