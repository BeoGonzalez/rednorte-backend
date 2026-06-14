package com.example.bff.controller;

import com.example.bff.client.ChatClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bff/chat")
public class BffChatController {

    @Autowired
    private ChatClient chatClient;

    @PostMapping("/preguntar")
    public ResponseEntity<?> preguntar(@RequestBody Map<String, Object> request) {
        try {
            return chatClient.preguntar(request);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}
