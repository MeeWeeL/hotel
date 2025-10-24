package com.acme.hotel.config;

import com.acme.hotel.model.Hotel;
import com.acme.hotel.repo.HotelRepo;
import com.acme.hotel.service.RoomService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner initHotels(HotelRepo hotels, RoomService rooms) {
        return args -> {
            if (hotels.count() == 0) {
                var h1 = hotels.save(Hotel.builder().name("Sunrise").address("1 Beach Ave").build());
                var h2 = hotels.save(Hotel.builder().name("Mountain View").address("99 Peak Rd").build());
                rooms.createRoom(h1.getId(), "101", true);
                rooms.createRoom(h1.getId(), "102", true);
                rooms.createRoom(h2.getId(), "201", true);
                rooms.createRoom(h2.getId(), "202", true);
            }
        };
    }
}
