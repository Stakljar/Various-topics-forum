package com.example.various_topics_forum.comment;

import org.springframework.stereotype.Repository;

import com.example.various_topics_forum.discussion.Discussion;
import com.example.various_topics_forum.user.User;

import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndDeletedAtNull(Long commentId);

    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.deletedAt = :deletedAt WHERE c.discussion = :discussion AND c.deletedAt IS NULL")
    int softDeleteByDiscussion(Discussion discussion, Date deletedAt);

    @Query("SELECT new com.example.various_topics_forum.comment.CommentWithVotes(c, " +
            "SUM(CASE WHEN cv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN cv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "NULL) " +
            "FROM Comment c " +
            "LEFT JOIN CommentVote cv ON c.id = cv.comment.id " +
            "WHERE c.discussion = :discussion " +
            "GROUP BY c.id ")
     Page<CommentWithVotes> findWithVotesByDiscussion(Discussion discussion, Pageable pageable);

    @Query("SELECT new com.example.various_topics_forum.comment.CommentWithVotes(c, " +
            "SUM(CASE WHEN cv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN cv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "(SELECT cv_user.isUpvote " +
            " FROM CommentVote cv_user " +
            " WHERE cv_user.comment = c " +
            " AND cv_user.user = :user " +
            ")) " +
            "FROM Comment c " +
            "LEFT JOIN CommentVote cv ON c.id = cv.comment.id " +
            "WHERE c.discussion = :discussion " +
            "GROUP BY c.id ")
    Page<CommentWithVotes> findWithVotesByDiscussion(Discussion discussion,
            User user, Pageable pageable);

    @Query("SELECT new com.example.various_topics_forum.comment.CommentWithVotes(c, " +
            "SUM(CASE WHEN cv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN cv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "NULL) " +
            "FROM Comment c " +
            "LEFT JOIN CommentVote cv ON c.id = cv.comment.id " +
            "WHERE c.user = :user AND c.deletedAt IS NULL " +
            "GROUP BY c.id ")
     Page<CommentWithVotes> findWithVotesByUserDeletedAtNull(User user, Pageable pageable);
}
