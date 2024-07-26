package com.example.various_topics_forum.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequest {
    @Email
    private String email;

    @NotNull
    private String password;
}
