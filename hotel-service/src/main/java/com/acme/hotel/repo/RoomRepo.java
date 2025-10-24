package com.acme.hotel.repo;

import com.acme.hotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepo extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);
}
