package com.acme.booking.controller;

import com.acme.booking.dto.UserDtos.CreateUser;
import com.acme.booking.dto.UserDtos.UpdateUser;
import com.acme.booking.model.User;
import com.acme.booking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService svc;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public User create(@Valid @RequestBody CreateUser dto) {
        return svc.register(dto.username(), dto.password(), dto.role());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping
    public User update(@Valid @RequestBody UpdateUser dto) {
        return svc.update(dto.id(), dto.password(), dto.role());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public void delete(@RequestParam Long id) {
        svc.delete(id);
    }
}
