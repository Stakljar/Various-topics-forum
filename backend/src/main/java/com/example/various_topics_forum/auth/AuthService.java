package com.example.various_topics_forum.auth;

import com.example.various_topics_forum.auth.dto.AuthRequest;
import com.example.various_topics_forum.auth.dto.AuthResponse;
import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.auth.dto.RegistrationRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthResponse authenticateUser(AuthRequest request, HttpServletResponse response);

    IdResponse registerUser(RegistrationRequest registrationRequest);

    void resendConfirmationEmail(String email);

    void sendPasswordResetEmail(String email);

    AuthResponse confirmRegistrationEmail(String email, String token, HttpServletResponse response);

    void resetPassword(String email, String token, String newPassword);

    AuthResponse refreshUserAccessToken(String refreshToken);

    void setRefreshTokenCookie(HttpServletResponse response, String refreshToken);

    void sendEmailWithButton(String to, String token, String text, String buttonText, String link);
}
