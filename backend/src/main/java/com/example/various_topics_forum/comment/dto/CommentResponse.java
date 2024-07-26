package com.example.various_topics_forum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private Long userId;
    private Long discussionId;
    private String userProfileName;
    private String content;
    private String createdAt;
    private String deletedAt;

    @Builder.Default
    private Long upvoteCount = 0L;

    @Builder.Default
    private Long downvoteCount = 0L;

    private Boolean isUserUpvote;
    private Long parentCommentId;
    private Long parentCommentUserId;
    private String parentCommentUserProfileName;
    private String parentCommentContent;
    private String parentCommentCreatedAt;
    private String parentCommentDeletedAt;
}
