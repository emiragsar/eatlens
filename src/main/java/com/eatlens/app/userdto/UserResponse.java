package com.eatlens.app.userdto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role;
    private Boolean isActive;
    private String profileImageUrl;
    private LocalDateTime createdAt;
}