package com.example.various_topics_forum.discussion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscussionResponse {
    private Long id;
    private Long userId;
    private String userProfileName;
    private String title;
    private String description;
    private String category;
    private String createdAt;

    @Builder.Default
    private Long upvoteCount = 0L;

    @Builder.Default
    private Long downvoteCount = 0L;

    private Boolean isUserUpvote;
}
