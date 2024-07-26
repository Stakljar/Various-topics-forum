package com.example.various_topics_forum.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {
    @Email
    private String email;

    @Size(min = 8)
    private String password;

    @NotBlank
    private String profileName;
}
