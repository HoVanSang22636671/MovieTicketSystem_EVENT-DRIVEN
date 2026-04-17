package com.movieticket.contracts;

import java.time.Instant;

public class UserRegisteredEvent {
    private EventType eventType = EventType.USER_REGISTERED;
    private String userId;
    private String username;
    private Instant createdAt;

    public UserRegisteredEvent() {
    }

    public UserRegisteredEvent(String userId, String username, Instant createdAt) {
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
