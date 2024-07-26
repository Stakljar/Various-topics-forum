package com.example.various_topics_forum.auth.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailRequest {
    @Email
    private String email;
}
