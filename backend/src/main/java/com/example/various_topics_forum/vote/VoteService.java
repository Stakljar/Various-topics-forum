package com.example.various_topics_forum.vote;

import com.example.various_topics_forum.comment.Comment;
import com.example.various_topics_forum.discussion.Discussion;
import com.example.various_topics_forum.user.User;
import com.example.various_topics_forum.vote.dto.CommentVoteResponse;
import com.example.various_topics_forum.vote.dto.DiscussionVoteResponse;

public interface VoteService {

    DiscussionVoteResponse upvote(User user, Discussion discussion);

    DiscussionVoteResponse downvote(User user, Discussion discussion);

    CommentVoteResponse upvote(User user, Comment comment);

    CommentVoteResponse downvote(User user, Comment comment);
}
