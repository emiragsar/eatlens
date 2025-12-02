package com.eatlens.app.service;


import com.eatlens.app.dto.userdto.UserLoginRequest;
import com.eatlens.app.dto.userdto.UserRegisterRequest;
import com.eatlens.app.dto.userdto.UserResponse;
import com.eatlens.app.dto.userdto.UserUpdateRequest;

public interface UserService {
    UserResponse register(UserRegisterRequest request);
    UserResponse login(UserLoginRequest request);
    UserResponse getProfile(Long userId);
    UserResponse updateProfile(Long userId, UserUpdateRequest request);
    void changePassword(Long userId, String oldPassword, String newPassword);
    void deleteAccount(Long userId);
    boolean isEmailExists(String email);
}