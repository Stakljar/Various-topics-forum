package com.example.various_topics_forum.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithCounts {
    private User user;
    private Long discussionsCount = 0L;
    private Long commentsCount = 0L;
}
