package com.acme.booking;

import com.acme.booking.client.HotelClient;
import com.acme.booking.model.Booking;
import com.acme.booking.model.BookingStatus;
import com.acme.booking.repo.BookingRepo;
import com.acme.booking.service.BookingServiceCore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
class BookingServiceTests {

    @Autowired
    BookingRepo repo;
    @Autowired
    BookingServiceCore core;

    @MockBean
    HotelClient hotelClient;

    @Test
    void createBooking_success_and_idempotent() {
        Mockito.when(hotelClient.hold(anyLong(), any(), any(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
        Mockito.when(hotelClient.confirm(anyLong(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        var start = LocalDate.now().plusDays(2);
        var end = start.plusDays(2);
        var b1 = core.createBooking(1L, 1L, start, end, "req-100", "Bearer x");
        assertThat(b1.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        var b2 = core.createBooking(1L, 1L, start, end, "req-100", "Bearer x");
        assertThat(b2.getId()).isEqualTo(b1.getId());
        assertThat(repo.count()).isEqualTo(1);
    }

    @Test
    void createBooking_failure_compensate() {
        Mockito.when(hotelClient.hold(anyLong(), any(), any(), anyString(), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("down")));
        var start = LocalDate.now().plusDays(2);
        var end = start.plusDays(2);
        Booking b = core.createBooking(1L, 1L, start, end, "req-200", "Bearer x");
        assertThat(b.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }
}
