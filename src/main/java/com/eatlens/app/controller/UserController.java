package com.eatlens.app.controller;


import com.eatlens.app.dto.userdto.*;
import com.eatlens.app.security.CustomUserDetails;
import com.eatlens.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;


    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        UserResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader("userId") Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> changePassword(
            @RequestHeader("userId") Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(userId, oldPassword, newPassword);
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Şifre başarıyla değiştirildi");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> deleteAccount(@RequestHeader("userId") Long userId) {
        userService.deleteAccount(userId);
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Hesap başarıyla silindi");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return ResponseEntity.ok(exists);
    }
}