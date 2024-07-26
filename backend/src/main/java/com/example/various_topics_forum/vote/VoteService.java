package com.example.various_topics_forum.vote;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.various_topics_forum.comment.Comment;
import com.example.various_topics_forum.discussion.Discussion;
import com.example.various_topics_forum.user.User;
import com.example.various_topics_forum.vote.dto.CommentVoteResponse;
import com.example.various_topics_forum.vote.dto.DiscussionVoteResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final DiscussionVoteRepository discussionVoteRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final VoteMapper voteMapper;

    public DiscussionVoteResponse upvote(User user, Discussion discussion) {
        return vote(user, discussion, true);
    }

    public DiscussionVoteResponse downvote(User user, Discussion discussion) {
        return vote(user, discussion, false);
    }
    
    public CommentVoteResponse upvote(User user, Comment comment) {
        return vote(user, comment, true);
    }

    public CommentVoteResponse downvote(User user, Comment comment) {
        return vote(user, comment, false);
    }

    private DiscussionVoteResponse vote(User user, Discussion discussion, boolean isUpvote) {
        Optional<DiscussionVote> existingVote = discussionVoteRepository.findByUserAndDiscussion(user, discussion);
        if (existingVote.isPresent()) {
            DiscussionVote vote = existingVote.get();
            if (vote.getIsUpvote() == isUpvote) {
                discussionVoteRepository.delete(vote);
                return new DiscussionVoteResponse(vote.getDiscussion().getId(), null);
            }
            else {
                vote.setIsUpvote(isUpvote);
                DiscussionVote savedVote = discussionVoteRepository.save(vote);
                return voteMapper.toDiscussionVoteResponse(savedVote);
            }
        }
        else {
            DiscussionVote vote = new DiscussionVote(new DiscussionVoteId(user.getId(), discussion.getId()), user, discussion, isUpvote);
            DiscussionVote savedVote = discussionVoteRepository.save(vote);
            return voteMapper.toDiscussionVoteResponse(savedVote);
        }
    }
    
    private CommentVoteResponse vote(User user, Comment comment, boolean isUpvote) {
        Optional<CommentVote> existingVote = commentVoteRepository.findByUserAndComment(user, comment);
        if (existingVote.isPresent()) {
            CommentVote vote = existingVote.get();
            if (vote.getIsUpvote() == isUpvote) {
                commentVoteRepository.delete(vote);
                return new CommentVoteResponse(vote.getComment().getId(), null);
            } 
            else {
                vote.setIsUpvote(isUpvote);
                CommentVote savedVote = commentVoteRepository.save(vote);
                return voteMapper.toCommentVoteResponse(savedVote);
            }
        }
        else {
            CommentVote vote = new CommentVote(new CommentVoteId(user.getId(), comment.getId()), user, comment, isUpvote);
            CommentVote savedVote = commentVoteRepository.save(vote);
            return voteMapper.toCommentVoteResponse(savedVote);
        }
    }
}
