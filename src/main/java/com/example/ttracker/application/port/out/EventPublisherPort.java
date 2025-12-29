package com.example.ttracker.application.port.out;

public interface EventPublisherPort {
    void publish(Object event);
}
