package com.example.various_topics_forum.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String profileName;
    private String createdAt;

    @Builder.Default
    private Integer discussionsCount = 0;

    @Builder.Default
    private Integer commentsCount = 0;
}
