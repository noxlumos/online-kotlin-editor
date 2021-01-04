package com.example.kotlinIDE.controller;

import com.example.kotlinIDE.OutputEvent;
import com.example.kotlinIDE.OutputEventPublisher;
import com.example.kotlinIDE.service.KotlinIdeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URLDecoder;

@RestController
public class KotlinIdeController {

    private final Flux<OutputEvent> events;
    private final ObjectMapper objectMapper;
    private final KotlinIdeService kotlinIdeService;

    public KotlinIdeController(OutputEventPublisher eventPublisher, ObjectMapper objectMapper, KotlinIdeService kotlinIdeService) {
        this.events = Flux.create(eventPublisher).share();
        this.objectMapper = objectMapper;
        this.kotlinIdeService = kotlinIdeService;
    }

    @PostMapping(path = "/script")
    @CrossOrigin(origins = "*")
    public void input(@RequestParam("script") String script){
        try {
            String decodedScript = URLDecoder.decode(script, "UTF-8");
            kotlinIdeService.runScript(decodedScript);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/getOutput", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "*")
    public Flux<String> output() {
        return this.events.map(pce -> {
            try {
                return objectMapper.writeValueAsString(pce) + "\n\n";
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
