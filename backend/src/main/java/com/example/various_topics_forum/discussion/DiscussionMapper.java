package com.example.various_topics_forum.discussion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.discussion.dto.DiscussionPageResponse;
import com.example.various_topics_forum.discussion.dto.DiscussionRequest;
import com.example.various_topics_forum.discussion.dto.DiscussionResponse;

@Mapper(componentModel = "spring")
public interface DiscussionMapper {
    DiscussionMapper INSTANCE = Mappers.getMapper(DiscussionMapper.class);

    Discussion toDiscussion(DiscussionRequest discussionRequest);
    IdResponse toIdResponse(Discussion discussion);

    @Mapping(source = "content", target = "content")
    DiscussionPageResponse toDiscussionPageResponse(Page<Discussion> discussionPage);

    @Mapping(source = "content", target = "content")
    DiscussionPageResponse toDiscussionWithVotesPageResponse(Page<DiscussionWithVotes> discussionWithVotesPage);

    @Mapping(source = "discussion.user.id", target = "userId")
    @Mapping(source = "discussion.user.profileName", target = "userProfileName")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "dd.MM.YYYY. HH:mm:ss")
    DiscussionResponse toDiscussionResponse(Discussion discussion);

    @Mapping(source = "discussion.id", target = "id")
    @Mapping(source = "discussion.user.id", target = "userId")
    @Mapping(source = "discussion.user.profileName", target = "userProfileName")
    @Mapping(source = "discussion.title", target = "title")
    @Mapping(source = "discussion.description", target = "description")
    @Mapping(source = "discussion.category", target = "category")
    @Mapping(source = "discussion.createdAt", target = "createdAt", dateFormat = "dd.MM.YYYY. HH:mm:ss")
    DiscussionResponse toDiscussionWithVotesResponse(DiscussionWithVotes discussionWithVotes);
}
