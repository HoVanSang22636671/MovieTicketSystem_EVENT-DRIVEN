package com.movieticket.contracts;

public final class RabbitConstants {
    private RabbitConstants() {
    }

    public static final String EXCHANGE_MOVIE_TICKET = "movie.ticket.exchange";

    public static final String RK_USER_REGISTERED = "user.registered";
    public static final String RK_BOOKING_CREATED = "booking.created";
    public static final String RK_PAYMENT_COMPLETED = "payment.completed";
    public static final String RK_BOOKING_FAILED = "booking.failed";

    public static final String Q_PAYMENT_BOOKING_CREATED = "payment.booking.created.queue";

    public static final String Q_BOOKING_PAYMENT_COMPLETED = "booking.payment.completed.queue";
    public static final String Q_BOOKING_FAILED = "booking.booking.failed.queue";

    public static final String Q_NOTIFICATION_PAYMENT_COMPLETED = "notification.payment.completed.queue";
    public static final String Q_NOTIFICATION_BOOKING_FAILED = "notification.booking.failed.queue";
}
