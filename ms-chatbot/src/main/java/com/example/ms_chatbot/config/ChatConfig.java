package com.example.ms_chatbot.config;

import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public OpenAiChatOptions openAiChatOptions() {
        return OpenAiChatOptions.builder()
                .model("gpt-4o")      // Cambia .withModel por .model
                .temperature(0.7)     // Cambia .withTemperature por .temperature
                .build();
    }
}