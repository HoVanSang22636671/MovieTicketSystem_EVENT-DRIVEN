package com.movieticket.bookings.repo;

import com.movieticket.bookings.domain.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<Booking, String> {
}
