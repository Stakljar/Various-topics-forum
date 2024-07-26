package com.example.various_topics_forum.discussion.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscussionPageResponse {
    private long totalElements;
    private long totalPages;
    private boolean last;

    @Builder.Default
    private List<DiscussionResponse> content = new ArrayList<>();
}