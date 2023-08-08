package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedService;
import com.teamseven.MusicVillain.Interaction.InteractionService;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberService;
import com.teamseven.MusicVillain.Record.Record;
import com.teamseven.MusicVillain.Record.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DatabaseViewContorller {
    private final MemberService memberService;
    private final FeedService feedService;
    private final RecordService recordService;
    private final InteractionService interactionService;

    @Autowired
    public DatabaseViewContorller(MemberService memberService, FeedService feedService, RecordService recordService, InteractionService interactionService) {
        this.memberService = memberService;
        this.feedService = feedService;
        this.recordService = recordService;
        this.interactionService = interactionService;
    }

    @GetMapping("/view/members")
    public String memberView(Model model) {
        List<Member> memberList = memberService.getAllMembers();
        model.addAttribute("memberList", memberList);
        return "member_view";
    }

    @GetMapping("/view/feeds")
    public String feedView(Model model){
        List<Feed> feedList = feedService.getAllFeeds();
        model.addAttribute("feedList", feedList);
        return "feed_view";
    }

    @GetMapping("/view/interactions")
    public String interactionView(Model model){
        List<com.teamseven.MusicVillain.Interaction.Interaction> interactionList = interactionService.getAllInteractions();
        model.addAttribute("interactionList", interactionList);
        return "interaction_view";
    }

    @GetMapping("/view/records")
    public String recordsView(Model model){
        List<Record> recordList = recordService.getAllRecords();
        model.addAttribute("recordList", recordList);
        return "record_view";
    }
}
