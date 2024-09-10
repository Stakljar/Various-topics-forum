package com.example.various_topics_forum.discussion;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.comment.CommentRepository;
import com.example.various_topics_forum.discussion.dto.DiscussionPageResponse;
import com.example.various_topics_forum.discussion.dto.DiscussionRequest;
import com.example.various_topics_forum.discussion.dto.DiscussionResponse;
import com.example.various_topics_forum.user.User;
import com.example.various_topics_forum.vote.VoteService;
import com.example.various_topics_forum.vote.dto.DiscussionVoteRequest;
import com.example.various_topics_forum.vote.dto.DiscussionVoteResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscussionServiceImpl implements DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final CommentRepository commentRepository;
    private final VoteService voteService;
    private final DiscussionMapper discussionMapper;

    private Sort getSort(String sortBy) {
        if (sortBy == null || "popularity_desc".equalsIgnoreCase(sortBy)) {
            return JpaSort.unsafe("COUNT(dv.id)").descending().and(Sort.by("d.createdAt").descending());
        }
        else if ("popularity_asc".equalsIgnoreCase(sortBy)) {
            return JpaSort.unsafe("COUNT(dv.id)").ascending().and(Sort.by("d.createdAt").descending());
        }
        else if ("date_desc".equalsIgnoreCase(sortBy)) {
            return Sort.by("d.createdAt").descending().and(JpaSort.unsafe("COUNT(dv.id)").descending());
        }
        else if ("date_asc".equalsIgnoreCase(sortBy)) {
            return Sort.by("d.createdAt").ascending().and(JpaSort.unsafe("COUNT(dv.id)").descending());
        }
        else {
            throw new IllegalArgumentException("Invalid sort parameter: " + sortBy);
        }
    }

    @Override
    public IdResponse addDiscussion(DiscussionRequest discussionRequest) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Discussion discussion = discussionMapper.toDiscussion(discussionRequest);
        discussion.setUser(user);
        Discussion savedDiscussion = discussionRepository.save(discussion);
        return discussionMapper.toIdResponse(savedDiscussion);
    }

    @Override
    public DiscussionVoteResponse voteDiscussion(DiscussionVoteRequest discussionVoteRequest) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Discussion discussion = discussionRepository.findByIdAndDeletedAtNull(discussionVoteRequest.getDiscussionId()).orElseThrow(
                () -> new EntityNotFoundException("Discussion with id " + discussionVoteRequest.getDiscussionId() + " not found"));
        return discussionVoteRequest.getIsUpvote() ? voteService.upvote(user, discussion) : voteService.downvote(user, discussion);
    }

    @Override
    @Transactional
    public IdResponse removeDiscussion(Long discussionId) {
        Discussion discussion = discussionRepository.findByIdAndDeletedAtNull(discussionId).orElseThrow(
                () -> new EntityNotFoundException("Discussion with id " + discussionId + " not found"));
        discussion.setDeletedAt(new Date());
        commentRepository.softDeleteByDiscussion(discussion, new Date());
        return discussionMapper.toIdResponse(discussionRepository.save(discussion));
    }

    @Override
    public boolean isOwner(Long discussionId) {
        Discussion discussion = discussionRepository.findById(discussionId).orElse(null);
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (discussion == null || discussion.getUser() == null) {
            return false;
        }
        return discussion.getUser().getUsername().equals(user.getUsername());
    }

    @Override
    public DiscussionResponse fetchActiveDiscussionById(Long discussionId) {
        return discussionMapper.toDiscussionWithVotesResponse(
                discussionRepository.findWithVotesByIdDeletedAtNull(discussionId).orElseThrow(
                        () -> new EntityNotFoundException("Discussion with id " + discussionId + " not found")));
    }

    @Override
    public DiscussionResponse fetchActiveDiscussionWithUserVotesById(Long discussionId) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return discussionMapper.toDiscussionWithVotesResponse(discussionRepository.findWithVotesByIdDeletedAtNull(discussionId, user).orElseThrow(
                () -> new EntityNotFoundException("Discussion with id " + discussionId + " not found")));
    }

    @Override
    public DiscussionPageResponse fetchActiveDiscussionsByUser(int pageNumber, int pageSize) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("d.createdAt").descending());
        return discussionMapper.toDiscussionWithVotesPageResponse(
                discussionRepository.findWithVotesByUserDeletedAtNull(user, pageable));
    }

    @Override
    public DiscussionPageResponse fetchActiveDiscussionsBySearchAndCategory(String searchTerm, Category category, 
            int pageNumber, int pageSize, String sortBy) {
        Sort sort = getSort(sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        if (searchTerm != null && category != null) {
            Page<DiscussionWithVotes> discussionWithVotesPage = discussionRepository.findWithVotesByCategoryAndTitleContainingAndDeletedAtNull(
                    searchTerm, category, pageable);
            return discussionMapper.toDiscussionWithVotesPageResponse(discussionWithVotesPage);
        }
        else if (searchTerm != null) {
            Page<DiscussionWithVotes> discussionWithVotesPage = discussionRepository.findWithVotesTitleContainingAndDeletedAtNull(
                    searchTerm, pageable);
            return discussionMapper.toDiscussionWithVotesPageResponse(discussionWithVotesPage);
        }
        else if (category != null) {
            Page<DiscussionWithVotes> discussionWithVotesPage = discussionRepository.findWithVotesByCategoryAndDeletedAtNull(
                    category, pageable);
            return discussionMapper.toDiscussionWithVotesPageResponse(discussionWithVotesPage);
        }
        Page<DiscussionWithVotes> discussionWithVotesPage = discussionRepository.findWithVotesDeletedAtNull(pageable);
        return discussionMapper.toDiscussionWithVotesPageResponse(discussionWithVotesPage);
    }

    @Override
    public DiscussionPageResponse fetchActiveDiscussionsWithUserVotesBySearchAndCategory(String searchTerm, Category category, 
            int pageNumber, int pageSize, String sortBy) {
        Sort sort = getSort(sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (searchTerm != null && category != null) {
            Page<DiscussionWithVotes> discussionWithVotesPage = discussionRepository.findWithVotesByCategoryAndTitleContainingAndDeletedAtNull(
                    user, searchTerm, category, pageable);
            return discussionMapper.toDiscussionWithVotesPageResponse(discussionWithVotesPage);
        }
        else if (searchTerm != null) {
            Page<DiscussionWithVotes> discussionWithVotesPage = discussionRepository.findWithVotesTitleContainingAndDeletedAtNull(
                    user, searchTerm, pageable);
            return discussionMapper.toDiscussionWithVotesPageResponse(discussionWithVotesPage);
        }
        else if (category != null) {
            Page<DiscussionWithVotes> discussionWithVotesPage = discussionRepository.findWithVotesByCategoryAndDeletedAtNull(
                    user, category, pageable);
            return discussionMapper.toDiscussionWithVotesPageResponse(discussionWithVotesPage);
        }
        Page<DiscussionWithVotes> discussionWithVotesPage = discussionRepository.findWithVotesDeletedAtNull(
                user, pageable);
        return discussionMapper.toDiscussionWithVotesPageResponse(discussionWithVotesPage);
    }
}
