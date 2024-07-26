package com.example.various_topics_forum.vote;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscussionVoteId implements Serializable {
    private static final long serialVersionUID = -5033025034916090368L;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "discussion_id")
    private Long discussionId;
}
