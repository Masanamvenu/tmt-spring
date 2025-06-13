package com.tmt.automation.controller;

import com.tmt.automation.model.User;
import com.tmt.automation.repository.UserRepository;
import com.tmt.automation.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8082") // Remove or restrict in production!
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public static class SignupRequest {
        public String email;
        public String password;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class AuthResponse {
        public String token;
        public String email;

        public AuthResponse(String token, String email) {
            this.token = token;
            this.email = email;
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.email)) {
            return ResponseEntity.badRequest().body("Email is already in use!");
        }
        User user = new User();
        user.setEmail(signupRequest.email);
        user.setPassword(passwordEncoder.encode(signupRequest.password));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(loginRequest.password, userOpt.get().getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        String token = jwtUtil.generateToken(loginRequest.email);
        return ResponseEntity.ok(new AuthResponse(token, loginRequest.email));
    }
}