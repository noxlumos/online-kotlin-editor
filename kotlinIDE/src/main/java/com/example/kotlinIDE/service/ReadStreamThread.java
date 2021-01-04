package com.example.kotlinIDE.service;

import com.example.kotlinIDE.OutputEvent;
import com.example.kotlinIDE.model.Output;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadStreamThread implements Runnable {

    private InputStream inputStream;
    private final ApplicationEventPublisher publisher;
    private String streamName;

    public ReadStreamThread(InputStream inputStream, ApplicationEventPublisher applicationEventPublisher, String streamName){
        this.inputStream = inputStream;
        this.publisher = applicationEventPublisher;
        this.streamName = streamName;
    }

    public void run(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while( (line = reader.readLine()) != null){
                this.publisher.publishEvent(new OutputEvent(new Output(line, streamName)));
            }
            this.publisher.publishEvent(new OutputEvent(new Output("finished", streamName)));
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
