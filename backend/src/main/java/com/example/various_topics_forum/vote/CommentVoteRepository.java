package com.example.various_topics_forum.vote;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.various_topics_forum.comment.Comment;
import com.example.various_topics_forum.user.User;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, CommentVoteId> {
    Optional<CommentVote> findByUserAndComment(User user, Comment comment);
}
