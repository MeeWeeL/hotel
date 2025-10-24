package com.acme.hotel.controller;

import com.acme.hotel.dto.CreateHotelDto;
import com.acme.hotel.dto.CreateRoomDto;
import com.acme.hotel.model.Hotel;
import com.acme.hotel.repo.HotelRepo;
import com.acme.hotel.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {
    private final HotelRepo hotels;
    private final RoomService rooms;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hotels")
    public Hotel createHotel(@Valid @RequestBody CreateHotelDto dto) {
        return hotels.save(
                Hotel.builder()
                        .name(dto.name())
                        .address(dto.address())
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/rooms")
    public Object createRoom(@Valid @RequestBody CreateRoomDto dto) {
        return rooms.createRoom(dto.hotelId(), dto.number(), dto.available());
    }
}
