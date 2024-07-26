package com.example.various_topics_forum.discussion;

import org.springframework.stereotype.Repository;

import com.example.various_topics_forum.user.User;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    Optional<Discussion> findByIdAndDeletedAtNull(Long id);

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "COALESCE(SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), 0), " +
            "NULL) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "WHERE d.id = :id AND d.deletedAt IS NULL " +
            "GROUP BY d ")
    Optional<DiscussionWithVotes> findWithVotesByIdDeletedAtNull(Long id);

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "COALESCE(SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), 0), " +
            "dv_user.isUpvote) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "LEFT JOIN DiscussionVote dv_user ON d.id = dv_user.discussion.id AND dv_user.user = :user " +
            "WHERE d.id = :id AND d.deletedAt IS NULL " +
            "GROUP BY d, dv_user.isUpvote")
    Optional<DiscussionWithVotes> findWithVotesByIdDeletedAtNull(Long id, User user);

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "NULL) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "WHERE d.user = :user AND d.deletedAt IS NULL " +
            "GROUP BY d.id ")
    Page<DiscussionWithVotes> findWithVotesByUserDeletedAtNull(User user, Pageable pageable);

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "NULL) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "WHERE d.deletedAt IS NULL " +
            "GROUP BY d.id ")
    Page<DiscussionWithVotes> findWithVotesDeletedAtNull(Pageable pageable);

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "(SELECT dv_user.isUpvote " +
            " FROM DiscussionVote dv_user " +
            " WHERE dv_user.discussion = d " +
            " AND dv_user.user = :user " +
            ")) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "WHERE d.deletedAt IS NULL " +
            "GROUP BY d.id ")
    Page<DiscussionWithVotes> findWithVotesDeletedAtNull(User user, Pageable pageable);

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "NULL) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "WHERE d.deletedAt IS NULL AND d.title LIKE %:searchTerm% " +
            "GROUP BY d.id ")
    Page<DiscussionWithVotes> findWithVotesTitleContainingAndDeletedAtNull(String searchTerm, Pageable pageable);

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "(SELECT dv_user.isUpvote " +
            " FROM DiscussionVote dv_user " +
            " WHERE dv_user.discussion = d " +
            " AND dv_user.user = :user " +
            ")) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "WHERE d.deletedAt IS NULL AND d.title LIKE %:searchTerm% " +
            "GROUP BY d.id ")
    Page<DiscussionWithVotes> findWithVotesTitleContainingAndDeletedAtNull(User user,
            String searchTerm, Pageable pageable);
    

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "NULL) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "WHERE d.deletedAt IS NULL AND d.category = :category " +
            "GROUP BY d.id ")
    Page<DiscussionWithVotes> findWithVotesByCategoryAndDeletedAtNull(Category category, Pageable pageable);

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "(SELECT dv_user.isUpvote " +
            " FROM DiscussionVote dv_user " +
            " WHERE dv_user.discussion = d " +
            " AND dv_user.user = :user " +
            ")) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "WHERE d.deletedAt IS NULL AND d.category = :category " +
            "GROUP BY d.id ")
    Page<DiscussionWithVotes> findWithVotesByCategoryAndDeletedAtNull(User user,
            Category category, Pageable pageable);

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "NULL) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "WHERE d.deletedAt IS NULL AND d.title LIKE %:searchTerm% AND d.category = :category " +
            "GROUP BY d.id ")
    Page<DiscussionWithVotes> findWithVotesByCategoryAndTitleContainingAndDeletedAtNull(String searchTerm,
            Category category, Pageable pageable);

    @Query("SELECT new com.example.various_topics_forum.discussion.DiscussionWithVotes(d, " +
            "SUM(CASE WHEN dv.isUpvote = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN dv.isUpvote = FALSE THEN 1 ELSE 0 END), " +
            "(SELECT dv_user.isUpvote " +
            " FROM DiscussionVote dv_user " +
            " WHERE dv_user.discussion = d " +
            " AND dv_user.user = :user " +
            ")) " +
            "FROM Discussion d " +
            "LEFT JOIN DiscussionVote dv ON d.id = dv.discussion.id " +
            "WHERE d.deletedAt IS NULL AND d.title LIKE %:searchTerm% AND d.category = :category " +
            "GROUP BY d.id ")
    Page<DiscussionWithVotes> findWithVotesByCategoryAndTitleContainingAndDeletedAtNull(User user,
            String searchTerm, Category category, Pageable pageable);
}
