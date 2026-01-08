package com.example.ttracker.adapters.out.events;

import com.example.ttracker.application.port.out.EventPublisherPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringEventPublisherAdapter implements EventPublisherPort {
    private final ApplicationEventPublisher publisher;

    public SpringEventPublisherAdapter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override public void publish(Object event) {
        publisher.publishEvent(event);
    }
}
