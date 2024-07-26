package com.example.various_topics_forum.discussion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscussionWithVotes {
    private Discussion discussion;
    private Long upvoteCount;
    private Long downvoteCount;
    private Boolean isUserUpvote;
}
