package com.examcell.admin.controller;

import com.examcell.admin.dto.AuthRequest;
import com.examcell.admin.dto.AuthResponse;
import com.examcell.admin.dto.SignupRequest;
// --- FIX: IMPORT THE CORRECT SERVICE ---
import com.examcell.admin.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // --- FIX: INJECT AuthService, NOT JwtService ---
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        // This line will now work because authService is of type AuthService
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        // This line will also work now
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}