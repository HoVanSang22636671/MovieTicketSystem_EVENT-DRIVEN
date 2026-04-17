package com.movieticket.contracts;

import java.time.Instant;

public class PaymentCompletedEvent {
    private EventType eventType = EventType.PAYMENT_COMPLETED;
    private String bookingId;
    private String paymentId;
    private String status = "SUCCESS";
    private Instant processedAt;

    public PaymentCompletedEvent() {
    }

    public PaymentCompletedEvent(String bookingId, String paymentId, String status, Instant processedAt) {
        this.bookingId = bookingId;
        this.paymentId = paymentId;
        this.status = status;
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

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
