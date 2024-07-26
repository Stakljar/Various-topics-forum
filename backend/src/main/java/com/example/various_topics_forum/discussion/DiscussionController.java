package com.example.various_topics_forum.discussion;

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
import com.example.various_topics_forum.discussion.dto.DiscussionPageResponse;
import com.example.various_topics_forum.discussion.dto.DiscussionRequest;
import com.example.various_topics_forum.discussion.dto.DiscussionResponse;
import com.example.various_topics_forum.vote.dto.DiscussionVoteRequest;
import com.example.various_topics_forum.vote.dto.DiscussionVoteResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/discussions")
@RequiredArgsConstructor
public class DiscussionController {
    private final DiscussionService discussionService;

    @PostMapping("/add")
    public ResponseEntity<IdResponse> addDiscussion(@Valid @RequestBody DiscussionRequest discussionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discussionService.addDiscussion(discussionRequest));
    }

    @PostMapping("/vote")
    public ResponseEntity<DiscussionVoteResponse> voteDiscussion(@Valid @RequestBody DiscussionVoteRequest discussionVoteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discussionService.voteDiscussion(discussionVoteRequest));
    }

    @GetMapping("/{discussionId}")
    public ResponseEntity<DiscussionResponse> fetchActiveDiscussionById(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionService.fetchActiveDiscussionById(discussionId));
    }

    @GetMapping("/user")
    public ResponseEntity<DiscussionPageResponse> fetchActiveDiscussionByUser(
            @RequestParam(name = "page_number", required = true) int pageNumber,
            @RequestParam(name = "page_size", required = true) int pageSize) {
        return ResponseEntity.ok(discussionService.fetchActiveDiscussionsByUser(pageNumber, pageSize));
    }

    @GetMapping("/with_user_votes/{discussionId}")
    public ResponseEntity<DiscussionResponse> fetchActiveDiscussionByIdWithUserVotes(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionService.fetchActiveDiscussionWithUserVotesById(discussionId));
    }

    @DeleteMapping("/remove/{discussionId}")
    @PreAuthorize("@discussionService.isOwner(#discussionId) or hasRole(T(com.example.various_topics_forum.user.Role).ADMIN.name())")
    public ResponseEntity<IdResponse> removeDiscussion(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionService.removeDiscussion(discussionId));
    }

    @GetMapping
    public ResponseEntity<DiscussionPageResponse> fetchActiveDiscussions(
            @RequestParam(name = "page_number", required = true) int pageNumber,
            @RequestParam(name = "page_size", required = true) int pageSize,
            @RequestParam(name = "search_term", required = false) String searchTerm,
            @RequestParam(name = "category", required = false) Category category,
            @RequestParam(name = "sort_by", required = false) String sortBy) {
        return ResponseEntity.ok(discussionService.fetchActiveDiscussionsBySearchAndCategory(
                searchTerm, category, pageNumber, pageSize, sortBy));
    }

    @GetMapping("/with_user_votes")
    public ResponseEntity<DiscussionPageResponse> fetchActiveDiscussionsWithUserVotes(
            @RequestParam(name = "page_number", required = true) int pageNumber,
            @RequestParam(name = "page_size", required = true) int pageSize,
            @RequestParam(name = "search_term", required = false) String searchTerm,
            @RequestParam(name = "category", required = false) Category category,
            @RequestParam(name = "sort_by", required = false) String sortBy) {
        return ResponseEntity.ok(discussionService.fetchActiveDiscussionsWithUserVotesBySearchAndCategory(
                searchTerm, category, pageNumber, pageSize, sortBy));
    }
}
