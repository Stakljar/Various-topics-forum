package com.example.various_topics_forum.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiscussionRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private String category;
}
