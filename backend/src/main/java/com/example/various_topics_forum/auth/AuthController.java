package com.example.various_topics_forum.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.various_topics_forum.auth.dto.AuthRequest;
import com.example.various_topics_forum.auth.dto.AuthResponse;
import com.example.various_topics_forum.auth.dto.ConfirmEmailRequest;
import com.example.various_topics_forum.auth.dto.EmailRequest;
import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.auth.dto.PasswordResetRequest;
import com.example.various_topics_forum.auth.dto.RegistrationRequest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest authRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.authenticateUser(authRequest, response);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<IdResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
    	IdResponse idResponse = authService.registerUser(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(idResponse);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshUserAccessToken(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        AuthResponse authResponse = authService.refreshUserAccessToken(refreshToken);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/signup/resend-email")
    public ResponseEntity<String> resendConfirmationEmail(@Valid @RequestBody EmailRequest emailRequest) {
        authService.resendConfirmationEmail(emailRequest.getEmail());
        return ResponseEntity.ok("success");
    }

    @PostMapping("/signup/confirm")
    public ResponseEntity<AuthResponse> confirmRegistrationEmail(@Valid @RequestBody ConfirmEmailRequest confirmEmailRequest,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.confirmRegistrationEmail(confirmEmailRequest.getEmail(), confirmEmailRequest.getToken(), response);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/send-password-reset-email")
    public ResponseEntity<String> sendPasswordResetEmail(@Valid @RequestBody EmailRequest emailRequest) {
        authService.sendPasswordResetEmail(emailRequest.getEmail());
        return ResponseEntity.ok("success");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        authService.resetPassword(passwordResetRequest.getEmail(), passwordResetRequest.getToken(), passwordResetRequest.getNewPassword());
        return ResponseEntity.ok("success");
    }
}
