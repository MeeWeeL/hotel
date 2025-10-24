package com.acme.hotel.controller;

import com.acme.hotel.dto.RoomDto;
import com.acme.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomQueryController {
    private final RoomService roomService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public List<RoomDto> all() {
        return roomService.findAll();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/recommend")
    public List<RoomDto> recommend(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return roomService.recommend(start, end);
    }
}
