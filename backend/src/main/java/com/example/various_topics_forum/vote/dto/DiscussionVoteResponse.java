package com.example.various_topics_forum.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscussionVoteResponse {
    private Long discussionId;
    private Boolean isUpvote;
}
