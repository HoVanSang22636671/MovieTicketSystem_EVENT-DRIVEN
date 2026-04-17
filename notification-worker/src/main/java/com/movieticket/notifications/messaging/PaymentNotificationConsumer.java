package com.movieticket.notifications.messaging;

import com.movieticket.contracts.BookingFailedEvent;
import com.movieticket.contracts.PaymentCompletedEvent;
import com.movieticket.contracts.RabbitConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentNotificationConsumer.class);

    @RabbitListener(queues = RabbitConstants.Q_NOTIFICATION_PAYMENT_COMPLETED)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("[NOTIFICATION] Booking {} payment SUCCESS (paymentId={})", event.getBookingId(), event.getPaymentId());
    }

    @RabbitListener(queues = RabbitConstants.Q_NOTIFICATION_BOOKING_FAILED)
    public void onBookingFailed(BookingFailedEvent event) {
        log.info("[NOTIFICATION] Booking {} payment FAILED (reason={})", event.getBookingId(), event.getReason());
    }
}
