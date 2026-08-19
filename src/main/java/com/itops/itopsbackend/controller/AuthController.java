package com.itops.itopsbackend.controller;

import com.itops.itopsbackend.dto.LoginRequest;
import com.itops.itopsbackend.entity.User;
import com.itops.itopsbackend.entity.UserRole;
import com.itops.itopsbackend.service.JwtService;
import com.itops.itopsbackend.service.UserService;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String email = payload.get("email");
        String password = payload.get("password");
        String department = payload.get("department");

        if (name == null || email == null || password == null || department == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields"));
        }

        Optional<User> existing = userService.findByEmail(email);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User already exists"));
        }

        User user = userService.registerUser(name, email, password, UserRole.EMPLOYEE, department);
        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "role", user.getRole().name(),
            "department", user.getDepartment()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        User user = userService.findByEmail(request.email()).orElseThrow();
        return ResponseEntity.ok(Map.of(
            "message", "Login successful",
            "token", jwtService.generateToken(user),
            "userId", user.getId(),
            "email", user.getEmail(),
            "role", user.getRole().name()
        ));
    }
}
