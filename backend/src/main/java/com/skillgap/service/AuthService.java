package com.skillgap.service;

import com.skillgap.dto.AuthResponse;
import com.skillgap.dto.LoginRequest;
import com.skillgap.dto.RegisterRequest;
import com.skillgap.exception.BadRequestException;
import com.skillgap.model.Role;
import com.skillgap.model.User;
import com.skillgap.repository.UserRepository;
import com.skillgap.security.JwtUtils;
import com.skillgap.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .build();
        userRepository.save(user);

        return authenticate(request.getUsername(), request.getPassword());
    }

    public AuthResponse login(LoginRequest request) {
        return authenticate(request.getUsername(), request.getPassword());
    }

    private AuthResponse authenticate(String username, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, rawPassword));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_STUDENT")
                .replace("ROLE_", "");

        return new AuthResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), role);
    }
}
