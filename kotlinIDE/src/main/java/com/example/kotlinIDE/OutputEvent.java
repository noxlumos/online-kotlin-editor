package com.example.kotlinIDE;

import com.example.kotlinIDE.model.Output;
import org.springframework.context.ApplicationEvent;

public class OutputEvent extends ApplicationEvent {

    public OutputEvent(Output newOutput){
        super(newOutput);
    }
}
