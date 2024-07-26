package com.example.various_topics_forum.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentVoteRequest {
    @NotNull
    private Long commentId;

    @NotNull
    private Boolean isUpvote;
}
