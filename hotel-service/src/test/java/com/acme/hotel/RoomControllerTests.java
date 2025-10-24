package com.acme.hotel;

import com.acme.hotel.dto.AvailabilityRequest;
import com.acme.hotel.model.Room;
import com.acme.hotel.repo.RoomRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerTests {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;
    @Autowired
    RoomRepo rooms;
    Long roomId;

    @BeforeEach
    void setUp() {
        roomId = rooms.findAll().stream().findFirst().map(Room::getId).orElseThrow();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void hold_thenConflictOnOverlap() throws Exception {
        var req = new AvailabilityRequest(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), "req-1");
        mvc.perform(post("/internal/rooms/{id}/confirm-availability", roomId)
                        .contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req)))
                .andExpect(status().isOk());

        // same dates, different requestId -> conflict 409
        var req2 = new AvailabilityRequest(req.startDate(), req.endDate(), "req-2");
        mvc.perform(post("/internal/rooms/{id}/confirm-availability", roomId)
                        .contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req2)))
                .andExpect(status().isConflict());
    }
}
