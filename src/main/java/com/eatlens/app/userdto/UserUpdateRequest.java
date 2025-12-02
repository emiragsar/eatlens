package com.eatlens.app.userdto;


import lombok.Data;

@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profileImageUrl;
}