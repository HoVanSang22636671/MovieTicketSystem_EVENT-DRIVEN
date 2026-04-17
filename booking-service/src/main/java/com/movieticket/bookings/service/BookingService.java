package com.movieticket.bookings.service;

import com.movieticket.bookings.domain.Booking;
import com.movieticket.bookings.domain.BookingStatus;
import com.movieticket.bookings.repo.BookingRepository;
import com.movieticket.bookings.web.dto.CreateBookingRequest;
import com.movieticket.contracts.BookingCreatedEvent;
import com.movieticket.contracts.RabbitConstants;
import java.time.Instant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RabbitTemplate rabbitTemplate;

    public BookingService(BookingRepository bookingRepository, RabbitTemplate rabbitTemplate) {
        this.bookingRepository = bookingRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Booking createBooking(CreateBookingRequest request) {
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setMovieId(request.getMovieId());
        booking.setSeatNumber(request.getSeatNumber());
        booking.setAmount(request.getAmount());
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());

        booking = bookingRepository.save(booking);

        BookingCreatedEvent event = new BookingCreatedEvent(
                booking.getId(),
                booking.getUserId(),
                booking.getMovieId(),
                booking.getSeatNumber(),
                booking.getAmount(),
                booking.getCreatedAt()
        );

        rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_MOVIE_TICKET, RabbitConstants.RK_BOOKING_CREATED, event);
        return booking;
    }
}
