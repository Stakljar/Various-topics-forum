package com.example.various_topics_forum.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentWithVotes {
    private Comment comment;
    private Long upvoteCount;
    private Long downvoteCount;
    private Boolean isUserUpvote;
}
