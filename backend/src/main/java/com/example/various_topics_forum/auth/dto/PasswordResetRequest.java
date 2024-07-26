package com.example.various_topics_forum.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @Email
    private String email;

    @NotBlank
    private String token;

    @Size(min = 8)
    private String newPassword;
}
