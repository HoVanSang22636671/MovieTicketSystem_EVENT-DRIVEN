package com.movieticket.payments.messaging;

import com.movieticket.contracts.BookingCreatedEvent;
import com.movieticket.contracts.BookingFailedEvent;
import com.movieticket.contracts.PaymentCompletedEvent;
import com.movieticket.contracts.RabbitConstants;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookingCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(BookingCreatedConsumer.class);

    private final RabbitTemplate rabbitTemplate;

    public BookingCreatedConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConstants.Q_PAYMENT_BOOKING_CREATED)
    public void onBookingCreated(BookingCreatedEvent event) {
        log.info("Received BOOKING_CREATED bookingId={}, amount={}, seat={}",
                event.getBookingId(), event.getAmount(), event.getSeatNumber());

        simulateProcessingDelay();

        boolean success = Math.random() >= 0.5;
        Instant now = Instant.now();

        if (success) {
            PaymentCompletedEvent completed = new PaymentCompletedEvent(
                    event.getBookingId(),
                    "p-" + UUID.randomUUID(),
                    "SUCCESS",
                    now
            );

            rabbitTemplate.convertAndSend(
                    RabbitConstants.EXCHANGE_MOVIE_TICKET,
                    RabbitConstants.RK_PAYMENT_COMPLETED,
                    completed
            );

            log.info("Published PAYMENT_COMPLETED bookingId={}, paymentId={}", completed.getBookingId(), completed.getPaymentId());
        } else {
            BookingFailedEvent failed = new BookingFailedEvent(
                    event.getBookingId(),
                    "PAYMENT_REJECTED",
                    now
            );

            rabbitTemplate.convertAndSend(
                    RabbitConstants.EXCHANGE_MOVIE_TICKET,
                    RabbitConstants.RK_BOOKING_FAILED,
                    failed
            );

            log.info("Published BOOKING_FAILED bookingId={}, reason={}", failed.getBookingId(), failed.getReason());
        }
    }

    private void simulateProcessingDelay() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
