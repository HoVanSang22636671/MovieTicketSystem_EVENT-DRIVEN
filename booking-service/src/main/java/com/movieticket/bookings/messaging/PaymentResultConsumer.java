package com.movieticket.bookings.messaging;

import com.movieticket.bookings.domain.Booking;
import com.movieticket.bookings.domain.BookingStatus;
import com.movieticket.bookings.repo.BookingRepository;
import com.movieticket.contracts.BookingFailedEvent;
import com.movieticket.contracts.PaymentCompletedEvent;
import com.movieticket.contracts.RabbitConstants;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);

    private final BookingRepository bookingRepository;

    public PaymentResultConsumer(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @RabbitListener(queues = RabbitConstants.Q_BOOKING_PAYMENT_COMPLETED)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        bookingRepository.findById(event.getBookingId()).ifPresent(booking -> {
            updateStatus(booking, BookingStatus.CONFIRMED);
            log.info("Booking {} CONFIRMED (paymentId={})", booking.getId(), event.getPaymentId());
        });
    }

    @RabbitListener(queues = RabbitConstants.Q_BOOKING_FAILED)
    public void onBookingFailed(BookingFailedEvent event) {
        bookingRepository.findById(event.getBookingId()).ifPresent(booking -> {
            updateStatus(booking, BookingStatus.FAILED);
            log.info("Booking {} FAILED (reason={})", booking.getId(), event.getReason());
        });
    }

    private void updateStatus(Booking booking, BookingStatus status) {
        booking.setStatus(status);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);
    }
}
