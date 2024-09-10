package com.example.various_topics_forum.comment;

import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.comment.dto.CommentPageResponse;
import com.example.various_topics_forum.comment.dto.CommentRequest;
import com.example.various_topics_forum.comment.dto.CommentResponse;
import com.example.various_topics_forum.vote.dto.CommentVoteRequest;
import com.example.various_topics_forum.vote.dto.CommentVoteResponse;

public interface CommentService {

    CommentResponse addComment(CommentRequest commentRequest);

    CommentVoteResponse voteComment(CommentVoteRequest commentVoteRequest);

    IdResponse removeComment(Long commentId);

    boolean isOwner(Long commentId);

    CommentPageResponse fetchActiveCommentsByUser(int pageNumber, int pageSize);

    CommentPageResponse fetchCommentsByDiscussion(Long discussionId, int pageNumber, int pageSize);

    CommentPageResponse fetchCommentsWithUserVotesByDiscussion(Long discussionId, int pageNumber, int pageSize);
}
