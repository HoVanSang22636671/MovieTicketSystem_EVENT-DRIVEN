package com.movieticket.contracts;

import java.time.Instant;

public class BookingFailedEvent {
    private EventType eventType = EventType.BOOKING_FAILED;
    private String bookingId;
    private String reason;
    private Instant processedAt;

    public BookingFailedEvent() {
    }

    public BookingFailedEvent(String bookingId, String reason, Instant processedAt) {
        this.bookingId = bookingId;
        this.reason = reason;
        this.processedAt = processedAt;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
