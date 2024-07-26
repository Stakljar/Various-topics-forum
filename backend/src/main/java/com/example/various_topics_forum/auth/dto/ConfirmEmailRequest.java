package com.example.various_topics_forum.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmEmailRequest {
    @Email
    private String email;

    @NotBlank
    private String token;
}
