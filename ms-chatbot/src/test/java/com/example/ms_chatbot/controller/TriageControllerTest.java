package com.example.ms_chatbot.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration; // 🟢 Añadimos esta importación
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TriageController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TriageControllerTest.MockAiConfig.class)
class TriageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testProcesarSintomas_Exitoso() throws Exception {
        String jsonValido = "{\"sintomas\": \"fiebre alta y dolor de cabeza\"}";

        mockMvc.perform(post("/api/chatbot/triage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isOk());
    }

    @Test
    void testProcesarSintomas_Invalido_Retorna400() throws Exception {
        String jsonInvalido = "{\"sintomas\": \"\"}";

        mockMvc.perform(post("/api/chatbot/triage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    // 🟢 CORRECCIÓN: Usar @TestConfiguration en lugar de @Configuration
    @TestConfiguration
    static class MockAiConfig {
        @Bean
        public ChatClient.Builder chatClientBuilder() {
            ChatClient mockChatClient = mock(ChatClient.class);
            ChatClient.Builder mockBuilder = mock(ChatClient.Builder.class);
            when(mockBuilder.build()).thenReturn(mockChatClient);

            ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
            ChatClient.CallResponseSpec callSpec = mock(ChatClient.CallResponseSpec.class);

            when(mockChatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.system(anyString())).thenReturn(requestSpec);
            when(requestSpec.user(anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callSpec);

            ChatResponse chatResponse = mock(ChatResponse.class);
            Generation generation = mock(Generation.class);
            AssistantMessage message = mock(AssistantMessage.class);
            ChatResponseMetadata metadata = mock(ChatResponseMetadata.class);
            Usage usage = mock(Usage.class);

            when(callSpec.chatResponse()).thenReturn(chatResponse);
            when(chatResponse.getResult()).thenReturn(generation);
            when(generation.getOutput()).thenReturn(message);
            when(message.getText()).thenReturn("Esta es una respuesta simulada por Mockito.");

            when(chatResponse.getMetadata()).thenReturn(metadata);
            when(metadata.getUsage()).thenReturn(usage);

            // Retornamos enteros normales (sin la L)
            when(usage.getPromptTokens()).thenReturn(10);
            when(usage.getCompletionTokens()).thenReturn(20);
            when(usage.getTotalTokens()).thenReturn(30);

            return mockBuilder;
        }
    }
}