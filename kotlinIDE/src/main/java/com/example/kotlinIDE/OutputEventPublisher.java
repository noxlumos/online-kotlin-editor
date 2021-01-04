package com.example.kotlinIDE;

import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Component
public class OutputEventPublisher implements ApplicationListener<OutputEvent>, // <1>
        Consumer<FluxSink<OutputEvent>> {

    private final Executor executor;
    private final BlockingQueue<OutputEvent> queue =
            new LinkedBlockingQueue<>();

    OutputEventPublisher(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onApplicationEvent(OutputEvent event) {
        this.queue.offer(event);
    }

    @Override
    public void accept(FluxSink<OutputEvent> sink) {
        this.executor.execute(() -> {
            while (true)
                try {
                    OutputEvent event = queue.take(); // <5>
                    sink.next(event); // <6>
                }
                catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }

}
