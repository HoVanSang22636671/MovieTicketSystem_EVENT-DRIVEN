package com.movieticket.contracts;

import java.time.Instant;

public class BookingCreatedEvent {
    private EventType eventType = EventType.BOOKING_CREATED;
    private String bookingId;
    private String userId;
    private String movieId;
    private String seatNumber;
    private long amount;
    private Instant createdAt;

    public BookingCreatedEvent() {
    }

    public BookingCreatedEvent(String bookingId, String userId, String movieId, String seatNumber, long amount, Instant createdAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.movieId = movieId;
        this.seatNumber = seatNumber;
        this.amount = amount;
        this.createdAt = createdAt;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
