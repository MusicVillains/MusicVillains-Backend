package com.teamseven.MusicVillain.Feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class FeedController {
    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService){
        this.feedService = feedService;
    }

}
