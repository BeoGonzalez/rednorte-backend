package com.example.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "ms-chatbot")
public interface ChatClient {

    @PostMapping("/api/chat/preguntar")
    ResponseEntity<?> preguntar(@RequestBody Map<String, Object> request);
}
