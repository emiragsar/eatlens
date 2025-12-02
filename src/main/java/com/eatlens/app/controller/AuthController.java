package com.eatlens.app.controller;


import com.eatlens.app.dto.jwt.JwtAuthenticationResponse;
import com.eatlens.app.dto.userdto.UserLoginRequest;
import com.eatlens.app.dto.userdto.UserRegisterRequest;
import com.eatlens.app.dto.userdto.UserResponse;
import com.eatlens.app.security.CustomUserDetails;
import com.eatlens.app.security.JwtTokenProvider;
import com.eatlens.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody UserLoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setAccessToken(jwt);
        response.setTokenType("Bearer");
        response.setId(userDetails.getId());
        response.setEmail(userDetails.getEmail());
        response.setFirstName(userDetails.getFirstName());
        response.setLastName(userDetails.getLastName());
        response.setRole(userDetails.getRole());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> registerUser(@Valid @RequestBody UserRegisterRequest registerRequest) {

        UserResponse user = userService.register(registerRequest);

        // Otomatik login
        String jwt = tokenProvider.generateTokenFromUserId(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setAccessToken(jwt);
        response.setTokenType("Bearer");
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}