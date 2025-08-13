package com.example.bankcards.controller;


import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.dto.UserUpdateDto;
import com.example.bankcards.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")

@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/my-info")
    public ResponseEntity<UserResponse> getMyUserInfo() {
        return ResponseEntity.ok(userService.getMyUserInfo());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserUpdateDto request,
                                                   @PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(userService.updateUser(request, userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "userId") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
