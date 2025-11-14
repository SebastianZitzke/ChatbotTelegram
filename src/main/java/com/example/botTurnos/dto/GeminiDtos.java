package com.example.botTurnos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Contiene todos los DTOs (Data Transfer Objects) para la API de Gemini.
 * Usamos records de Java 21 para clases de datos inmutables y concisas.
 * Usamos @JsonInclude para ignorar campos nulos al enviar JSON.
 */
public class GeminiDtos {

    // --- REQUEST DTOs (Lo que enviamos a Gemini) ---

    // El cuerpo principal de la solicitud
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GeminiRequest(List<Content> contents) {}

    // Contenedor del mensaje
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Content(List<Part> parts) {}

    // La parte de texto del mensaje
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Part(String text) {}

    // --- RESPONSE DTOs (Lo que recibimos de Gemini) ---

    // La respuesta principal
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GeminiResponse(List<Candidate> candidates) {}

    // Gemini puede devolver varias "candidatas" a respuesta
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Candidate(Content content) {}

    // NOTA: Reutilizamos las clases Content y Part para la respuesta.
}