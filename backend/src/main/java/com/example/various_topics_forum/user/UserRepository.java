package com.example.various_topics_forum.user;

import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);

    @Query("SELECT NEW com.example.various_topics_forum.user.UserWithCounts(u, " +
            "(SELECT COUNT(d) FROM u.discussions d WHERE d.deletedAt IS NULL), " +
            "(SELECT COUNT(c) FROM u.comments c WHERE c.deletedAt IS NULL)) " +
            "FROM User u " +
            "WHERE u.id = :id ")
    Optional<UserWithCounts> findWithCountsById(Long id);
}
