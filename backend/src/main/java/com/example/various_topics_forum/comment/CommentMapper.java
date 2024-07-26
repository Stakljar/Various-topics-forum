package com.example.various_topics_forum.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.comment.dto.CommentPageResponse;
import com.example.various_topics_forum.comment.dto.CommentRequest;
import com.example.various_topics_forum.comment.dto.CommentResponse;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    Comment toComment(CommentRequest commentRequest);
    IdResponse toIdResponse(Comment comment);

    @Mapping(source = "content", target = "content")
    CommentPageResponse toCommentPageResponse(Page<Comment> commentPage);

    @Mapping(source = "content", target = "content")
    CommentPageResponse toCommentWithVotesPageResponse(Page<CommentWithVotes> commentWithVotesPage);

    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "dd.MM.YYYY. HH:mm:ss")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.profileName", target = "userProfileName")
    @Mapping(source = "discussion.id", target = "discussionId")
    @Mapping(source = "parentComment.id", target = "parentCommentId")
    @Mapping(source = "parentComment.user.id", target = "parentCommentUserId")
    @Mapping(source = "parentComment.user.profileName", target = "parentCommentUserProfileName")
    @Mapping(source = "parentComment.content", target = "parentCommentContent")
    CommentResponse toCommentResponse(Comment comment);

    @Mapping(source = "comment.id", target = "id")
    @Mapping(target = "content", expression = "java(commentWithVotes.getComment().getDeletedAt() == null ? commentWithVotes.getComment().getContent() : null)")
    @Mapping(source = "comment.createdAt", target = "createdAt", dateFormat = "dd.MM.YYYY. HH:mm:ss")
    @Mapping(source = "comment.deletedAt", target = "deletedAt", dateFormat = "dd.MM.YYYY. HH:mm:ss")
    @Mapping(target = "userId", expression = "java((commentWithVotes.getComment().getUser() != null && "
            + "commentWithVotes.getComment().getDeletedAt() == null) ? commentWithVotes.getComment().getUser().getId() : null)")
    @Mapping(target = "userProfileName", expression = "java((commentWithVotes.getComment().getUser() != null && "
            + "commentWithVotes.getComment().getDeletedAt() == null) ? commentWithVotes.getComment().getUser().getProfileName() : null)")
    @Mapping(source = "comment.discussion.id", target = "discussionId")
    @Mapping(source = "comment.parentComment.id", target = "parentCommentId")
    @Mapping(target = "parentCommentUserId", expression = "java(commentWithVotes.getComment().getParentComment() == null ? null : "
            + "(commentWithVotes.getComment().getParentComment().getUser() != null && commentWithVotes.getComment().getParentComment().getDeletedAt() == null) ? "
            + "commentWithVotes.getComment().getParentComment().getUser().getId() : null)")
    @Mapping(target = "parentCommentUserProfileName", expression = "java(commentWithVotes.getComment().getParentComment() == null ? null : "
            + "(commentWithVotes.getComment().getParentComment().getUser() != null && commentWithVotes.getComment().getParentComment().getDeletedAt() == null) ? "
            + "commentWithVotes.getComment().getParentComment().getUser().getProfileName() : null)")
    @Mapping(target = "parentCommentContent", expression = "java(commentWithVotes.getComment().getParentComment() == null ? null : "
            + "commentWithVotes.getComment().getParentComment().getDeletedAt() == null ? commentWithVotes.getComment().getParentComment().getContent() : null)")
    @Mapping(source = "comment.parentComment.createdAt", target = "parentCommentCreatedAt", dateFormat = "dd.MM.YYYY. HH:mm:ss")
    @Mapping(source = "comment.parentComment.deletedAt", target = "parentCommentDeletedAt", dateFormat = "dd.MM.YYYY. HH:mm:ss")
    CommentResponse toCommentWithVotesResponse(CommentWithVotes commentWithVotes);
}
