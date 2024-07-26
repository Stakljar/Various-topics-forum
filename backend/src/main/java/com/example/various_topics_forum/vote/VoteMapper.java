package com.example.various_topics_forum.vote;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.various_topics_forum.vote.dto.CommentVoteResponse;
import com.example.various_topics_forum.vote.dto.DiscussionVoteResponse;

@Mapper(componentModel = "spring")
public interface VoteMapper {
    VoteMapper INSTANCE = Mappers.getMapper(VoteMapper.class);

    @Mapping(source = "discussion.id", target = "discussionId")
    DiscussionVoteResponse toDiscussionVoteResponse(DiscussionVote discussionVote);

    @Mapping(source = "comment.id", target = "commentId")
    CommentVoteResponse toCommentVoteResponse(CommentVote commentVote);
}
