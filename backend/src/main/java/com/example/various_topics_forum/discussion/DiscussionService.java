package com.example.various_topics_forum.discussion;

import com.example.various_topics_forum.auth.dto.IdResponse;
import com.example.various_topics_forum.discussion.dto.DiscussionPageResponse;
import com.example.various_topics_forum.discussion.dto.DiscussionRequest;
import com.example.various_topics_forum.discussion.dto.DiscussionResponse;
import com.example.various_topics_forum.vote.dto.DiscussionVoteRequest;
import com.example.various_topics_forum.vote.dto.DiscussionVoteResponse;

public interface DiscussionService {

    IdResponse addDiscussion(DiscussionRequest discussionRequest);

    DiscussionVoteResponse voteDiscussion(DiscussionVoteRequest discussionVoteRequest);
    
    IdResponse removeDiscussion(Long discussionId);

    boolean isOwner(Long discussionId);

    DiscussionResponse fetchActiveDiscussionById(Long discussionId);
    
    DiscussionResponse fetchActiveDiscussionWithUserVotesById(Long discussionId);

    DiscussionPageResponse fetchActiveDiscussionsByUser(int pageNumber, int pageSize);

    DiscussionPageResponse fetchActiveDiscussionsBySearchAndCategory(String searchTerm, Category category, 
            int pageNumber, int pageSize, String sortBy);

    DiscussionPageResponse fetchActiveDiscussionsWithUserVotesBySearchAndCategory(String searchTerm, Category category, 
            int pageNumber, int pageSize, String sortBy);
}
