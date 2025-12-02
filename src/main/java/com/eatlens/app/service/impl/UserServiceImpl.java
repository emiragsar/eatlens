package com.eatlens.app.service.impl;



import com.eatlens.app.dto.userdto.UserLoginRequest;
import com.eatlens.app.dto.userdto.UserRegisterRequest;
import com.eatlens.app.dto.userdto.UserResponse;
import com.eatlens.app.dto.userdto.UserUpdateRequest;
import com.eatlens.app.exception.BusinessException;
import com.eatlens.app.exception.ResourceNotFoundException;
import com.eatlens.app.mapper.EntityMapper;
import com.eatlens.app.model.User;
import com.eatlens.app.repository.UserRepository;
import com.eatlens.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(UserRegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Bu email adresi zaten kullanılıyor");
        }

        User user = mapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return mapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new BusinessException("Geçersiz email veya şifre"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Geçersiz email veya şifre");
        }

        log.info("User logged in successfully: {}", user.getEmail());
        return mapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        return mapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {}", userId);

        return mapper.toUserResponse(updatedUser);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Eski şifre yanlış");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed for user: {}", userId);
    }

    @Override
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);

        log.info("User account deleted: {}", userId);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}