package com.acme.booking.service;

import com.acme.booking.client.HotelClient;
import com.acme.booking.model.Booking;
import com.acme.booking.model.BookingStatus;
import com.acme.booking.repo.BookingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class BookingServiceCore {
    private final BookingRepo bookings;
    private final HotelClient hotelClient;

    @Transactional
    public Booking createBooking(
            Long userId,
            Long roomId,
            java.time.LocalDate start,
            java.time.LocalDate end,
            String requestId,
            String bearerToken
    ) {
        var existing = bookings.findByRequestId(requestId);
        if (existing.isPresent()) return existing.get();
        var booking = bookings.save(
                Booking.builder()
                        .userId(userId)
                        .roomId(roomId)
                        .startDate(start)
                        .endDate(end)
                .status(BookingStatus.PENDING).createdAt(OffsetDateTime.now()).requestId(requestId).build());
        try {
            hotelClient.hold(roomId, start, end, requestId, bearerToken).join();
            hotelClient.confirm(roomId, requestId, bearerToken).join();
            booking.setStatus(BookingStatus.CONFIRMED);
            return booking;
        } catch (CompletionException e) {
            try {
                hotelClient.release(roomId, requestId, bearerToken).join();
            } catch (Exception ignored) {
            }
            booking.setStatus(BookingStatus.CANCELLED);
            return booking;
        }
    }
}
