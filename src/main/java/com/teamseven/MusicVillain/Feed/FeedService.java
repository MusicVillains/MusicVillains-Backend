package com.teamseven.MusicVillain.Feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedService {
    private final FeedRepository feedRepository;

    @Autowired
    public FeedService(FeedRepository feedRepository){
        this.feedRepository = feedRepository;
    }
}
