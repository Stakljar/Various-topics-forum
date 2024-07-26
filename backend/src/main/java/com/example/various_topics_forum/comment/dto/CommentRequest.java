package com.example.various_topics_forum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {
    @NotNull
    private Long discussionId;

    private Long parentCommentId;
    
    @NotBlank
    private String content;
}
