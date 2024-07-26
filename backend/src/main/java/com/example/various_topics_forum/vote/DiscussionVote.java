package com.example.various_topics_forum.vote;

import com.example.various_topics_forum.discussion.Discussion;
import com.example.various_topics_forum.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionVote {
    @EmbeddedId
    private DiscussionVoteId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("discussionId")
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;

    @Column(nullable = false)
    private Boolean isUpvote;
}
