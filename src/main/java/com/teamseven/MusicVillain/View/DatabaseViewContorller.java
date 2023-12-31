package com.teamseven.MusicVillain.View;

import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Feed.FeedService;
import com.teamseven.MusicVillain.Interaction.Interaction;
import com.teamseven.MusicVillain.Interaction.InteractionService;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Member.MemberService;
import com.teamseven.MusicVillain.Notification.NotificationRepository;
import com.teamseven.MusicVillain.Notification.Notification;
import com.teamseven.MusicVillain.Record.Record;
import com.teamseven.MusicVillain.Record.RecordService;
import com.teamseven.MusicVillain.Withdrawal.Withdrawal;
import com.teamseven.MusicVillain.Withdrawal.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DatabaseViewContorller {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FeedService feedService;
    private final FeedRepository feedRepository;
    private final RecordService recordService;
    private final InteractionService interactionService;
    private final NotificationRepository notificationRepository;
    private final WithdrawalRepository withdrawalRepository;

    @Autowired
    public DatabaseViewContorller(MemberService memberService, FeedService feedService,
                                  RecordService recordService, InteractionService interactionService,
                                  FeedRepository feedRepository, NotificationRepository notificationRepository,
                                  MemberRepository memberRepository, WithdrawalRepository withdrawalRepository) {
        this.memberService = memberService;
        this.feedService = feedService;
        this.recordService = recordService;
        this.interactionService = interactionService;
        this.feedRepository = feedRepository;
        this.notificationRepository = notificationRepository;
        this.memberRepository = memberRepository;
        this.withdrawalRepository = withdrawalRepository;
    }

    @GetMapping("/view/withdrawals")
    public String withdrawalView(Model model) {
        List<Withdrawal> withdrawalList = withdrawalRepository.findAll();
        model.addAttribute("withdrawalList", withdrawalList);
        return "withdrawal_view";
    }

    @GetMapping("/view/members")
    public String memberView(Model model) {
        List<Member> memberList = memberRepository.findAll();
        model.addAttribute("memberList", memberList);
        return "member_view";
    }

    @GetMapping("/view/feeds")
    public String feedView(Model model){
        List<Feed> feedList = feedRepository.findAll();
        model.addAttribute("feedList", feedList);
        return "feed_view";
    }

    @GetMapping("/view/interactions")
    public String interactionView(Model model){
        List<Interaction> interactionList = interactionService.getAllInteractions();
        model.addAttribute("interactionList", interactionList);
        return "interaction_view";
    }

    @GetMapping("/view/records")
    public String recordsView(Model model){
        List<Record> recordList = recordService.getAllRecords();
        model.addAttribute("recordList", recordList);
        return "record_view";
    }
    @GetMapping("/view/notifications")
    public String notificationsView(Model model){
        List<Notification> notificationList = notificationRepository.findAll();
        model.addAttribute("notificationList", notificationList);
        return "notification_view";
    }

}
