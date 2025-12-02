package com.eatlens.app.security;


import com.eatlens.app.exception.ResourceNotFoundException;
import com.eatlens.app.model.User;
import com.eatlens.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Username olarak userId kullanıyoruz (JWT'den gelen)
        try {
            Long userId = Long.parseLong(username);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + userId));

            return CustomUserDetails.create(user);
        } catch (NumberFormatException e) {
            // Eğer email ile giriş yapılıyorsa
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + username));

            return CustomUserDetails.create(user);
        }
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + id));

        return CustomUserDetails.create(user);
    }
}
