package com.example.various_topics_forum.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.auth.dto.RegistrationRequest;
import com.example.various_topics_forum.user.dto.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(RegistrationRequest registrationRequest);
    IdResponse toIdResponse(User user);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.profileName", target = "profileName")
    @Mapping(source = "user.createdAt", target = "createdAt", dateFormat = "dd.MM.YYYY. HH:mm:ss")
    UserResponse toUserResponse(UserWithCounts userWithCounts);
}
