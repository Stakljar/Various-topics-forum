package com.example.various_topics_forum.vote;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.various_topics_forum.discussion.Discussion;
import com.example.various_topics_forum.user.User;

@Repository
public interface DiscussionVoteRepository extends JpaRepository<DiscussionVote, DiscussionVoteId> {
    Optional<DiscussionVote> findByUserAndDiscussion(User user, Discussion discussion);
}
