package com.example.various_topics_forum.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiscussionVoteRequest {
    @NotNull
    private Long discussionId;

    @NotNull
    private Boolean isUpvote;
}
