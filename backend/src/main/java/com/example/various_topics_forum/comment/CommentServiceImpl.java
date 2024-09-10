package com.example.various_topics_forum.comment;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.comment.dto.CommentPageResponse;
import com.example.various_topics_forum.comment.dto.CommentRequest;
import com.example.various_topics_forum.comment.dto.CommentResponse;
import com.example.various_topics_forum.discussion.Discussion;
import com.example.various_topics_forum.discussion.DiscussionRepository;
import com.example.various_topics_forum.user.User;
import com.example.various_topics_forum.vote.VoteService;
import com.example.various_topics_forum.vote.dto.CommentVoteRequest;
import com.example.various_topics_forum.vote.dto.CommentVoteResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final DiscussionRepository discussionRepository;
    private final VoteService voteService;

    @Override
    public CommentResponse addComment(CommentRequest commentRequest) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = commentMapper.toComment(commentRequest);
        if (commentRequest.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findByIdAndDeletedAtNull(commentRequest.getParentCommentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Parent comment with id " + commentRequest.getParentCommentId() + " not found"));
            comment.setParentComment(parentComment);
        }
        comment.setUser(user);
        comment.setDiscussion(discussionRepository.findByIdAndDeletedAtNull(commentRequest.getDiscussionId())
                .orElseThrow(() -> new EntityNotFoundException("Discussion with id " + commentRequest.getDiscussionId() + " not found")));
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    public CommentVoteResponse voteComment(CommentVoteRequest commentVoteRequest) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = commentRepository.findByIdAndDeletedAtNull(commentVoteRequest.getCommentId()).orElseThrow(() -> new EntityNotFoundException(
                "Comment with id " + commentVoteRequest.getCommentId() + " not found"));
        return commentVoteRequest.getIsUpvote() ? voteService.upvote(user, comment) : voteService.downvote(user, comment);
    }
    
    @Override
    public IdResponse removeComment(Long commentId) {
        Comment comment = commentRepository.findByIdAndDeletedAtNull(commentId).orElseThrow(() -> new EntityNotFoundException(
                "Comment with id " + commentId + " not found"));
        comment.setDeletedAt(new Date());
        return commentMapper.toIdResponse(commentRepository.save(comment));
    }

    @Override
    public boolean isOwner(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (comment == null) {
            return false;
        }
        return comment.getUser().getUsername().equals(user.getUsername());
    }
    
    @Override
    public CommentPageResponse fetchActiveCommentsByUser(int pageNumber, int pageSize) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("c.createdAt").descending());
        return commentMapper.toCommentWithVotesPageResponse(
                commentRepository.findWithVotesByUserDeletedAtNull(user, pageable));
    }

    @Override
    public CommentPageResponse fetchCommentsByDiscussion(Long discussionId, int pageNumber, int pageSize) {
        Discussion discussion = discussionRepository.findByIdAndDeletedAtNull(discussionId).orElseThrow(() -> new EntityNotFoundException(
                "Discussion with id " + discussionId + " not found"));
        Sort sort = Sort.by("c.createdAt").ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<CommentWithVotes> commentWithVotes = commentRepository.findWithVotesByDiscussion(discussion, pageable);
        return commentMapper.toCommentWithVotesPageResponse(commentWithVotes);
    }

    @Override
    public CommentPageResponse fetchCommentsWithUserVotesByDiscussion(Long discussionId, int pageNumber, int pageSize) {
        Discussion discussion = discussionRepository.findByIdAndDeletedAtNull(discussionId).orElseThrow(() -> new EntityNotFoundException(
                "Discussion with id " + discussionId + " not found"));
        Sort sort = Sort.by("c.createdAt").ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<CommentWithVotes> commentWithVotes = commentRepository.findWithVotesByDiscussion(discussion, user, pageable);
        return commentMapper.toCommentWithVotesPageResponse(commentWithVotes);
    }
}
