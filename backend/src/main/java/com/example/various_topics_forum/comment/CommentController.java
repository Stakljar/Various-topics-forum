package com.example.various_topics_forum.comment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.comment.dto.CommentPageResponse;
import com.example.various_topics_forum.comment.dto.CommentRequest;
import com.example.various_topics_forum.comment.dto.CommentResponse;
import com.example.various_topics_forum.vote.dto.CommentVoteRequest;
import com.example.various_topics_forum.vote.dto.CommentVoteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CommentRequest commentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(commentRequest));
    }

    @PostMapping("/vote")
    public ResponseEntity<CommentVoteResponse> voteComment(@Valid @RequestBody CommentVoteRequest commentVoteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.voteComment(commentVoteRequest));
    }

    @DeleteMapping("/remove/{commentId}")
    @PreAuthorize("@commentService.isOwner(#commentId) or hasRole(T(com.example.various_topics_forum.user.Role).ADMIN.name())")
    public ResponseEntity<IdResponse> removeComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.removeComment(commentId));
    }

    @GetMapping("/user")
    public ResponseEntity<CommentPageResponse> fetchActiveCommentsByUser(
            @RequestParam(name = "page_number", required = true) Integer pageNumber,
            @RequestParam(name = "page_size", required = true) Integer pageSize) {
        return ResponseEntity.ok(commentService.fetchActiveCommentsByUser(pageNumber, pageSize));
    }

    @GetMapping
    public ResponseEntity<CommentPageResponse> fetchCommentsByDiscussion(
            @RequestParam(name = "discussion_id", required = true) Long discussionId,
            @RequestParam(name = "page_number", required = true) Integer pageNumber,
            @RequestParam(name = "page_size", required = true) Integer pageSize) {
        return ResponseEntity.ok(commentService.fetchCommentsByDiscussion(discussionId, pageNumber, pageSize));
    }

    @GetMapping("/with_user_votes")
    public ResponseEntity<CommentPageResponse> fetchCommentsByDiscussionWithUserVotes(
            @RequestParam(name = "discussion_id", required = true) Long discussionId,
            @RequestParam(name = "page_number", required = true) Integer pageNumber,
            @RequestParam(name = "page_size", required = true) Integer pageSize) {
        return ResponseEntity.ok(commentService.fetchCommentsWithUserVotesByDiscussion(discussionId, pageNumber, pageSize));
    }
}
