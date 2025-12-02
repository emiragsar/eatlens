package com.eatlens.app.dto.userdto;


import lombok.Data;

@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profileImageUrl;
}