package com.example.botTurnos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * DTOs para la API de Groq (formato compatible con OpenAI).
 */
public class GroqDtos {

    // --- REQUEST DTOs (Lo que enviamos a Groq) ---

    /**
     * El cuerpo de la solicitud de chat.
     * @param model El modelo a usar (ej: "llama3-8b-8192")
     * @param messages La lista de mensajes de la conversación.
     * @param temperature Nivel de "creatividad" (0.7 es un buen valor).
     */
    public record GroqRequest(String model, List<Message> messages, double temperature) {}

    /**
     * Un solo mensaje en la conversación.
     * @param role El rol ("system", "user", o "assistant")
     * @param content El texto del mensaje.
     */
    public record Message(String role, String content) {}


    // --- RESPONSE DTOs (Lo que recibimos de Groq) ---

    /**
     * La respuesta principal de la API.
     */
    public record GroqResponse(List<Choice> choices) {}

    /**
     * La API puede devolver varias "opciones", nos quedamos con la primera.
     */
    public record Choice(Message message) {}

    // NOTA: Reutilizamos el DTO Message para la respuesta.
}