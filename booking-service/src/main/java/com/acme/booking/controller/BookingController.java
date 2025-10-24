package com.acme.booking.controller;

import com.acme.booking.client.HotelClient;
import com.acme.booking.dto.BookingDtos.CreateBookingRequest;
import com.acme.booking.model.Booking;
import com.acme.booking.repo.BookingRepo;
import com.acme.booking.repo.UserRepo;
import com.acme.booking.service.BookingServiceCore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BookingController {
    private final BookingServiceCore core;
    private final UserRepo users;
    private final BookingRepo bookings;
    private final HotelClient hotelClient;

    private String bearer(Jwt jwt) {
        return "Bearer " + jwt.getTokenValue();
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/booking")
    public Booking create(
            @Valid @RequestBody CreateBookingRequest req,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var user = users.findByUsername(jwt.getSubject()).orElseThrow();
        Long roomId = req.roomId();
        if (req.autoSelect()) {
            var rec = hotelClient.recommend(req.startDate(), req.endDate(), bearer(jwt));
            if (rec.isEmpty()) throw new IllegalArgumentException("No rooms available");
            roomId = rec.getFirst().id();
        }
        return core.createBooking(
                user.getId(),
                roomId,
                req.startDate(),
                req.endDate(),
                req.requestId(),
                bearer(jwt)
        );
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/bookings")
    public List<Booking> my(@AuthenticationPrincipal Jwt jwt) {
        var user = users.findByUsername(jwt.getSubject()).orElseThrow();
        return bookings.findByUserId(user.getId());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/booking/{id}")
    public Booking one(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var user = users.findByUsername(jwt.getSubject()).orElseThrow();
        var b = bookings.findById(id).orElseThrow();
        if (!b.getUserId().equals(user.getId())) throw new IllegalArgumentException("Forbidden");
        return b;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/booking/{id}")
    public Map<String, String> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var user = users.findByUsername(jwt.getSubject()).orElseThrow();
        var b = bookings.findById(id).orElseThrow();
        if (!b.getUserId().equals(user.getId())) throw new IllegalArgumentException("Forbidden");
        bookings.delete(b);
        return Map.of("status", "deleted");
    }
}
