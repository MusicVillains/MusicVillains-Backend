package com.teamseven.MusicVillain.Interaction;

import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Interaction.RequestBodyForm.InteractionCreationRequestBody;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final MemberRepository memberRepository;
    private final FeedRepository feedRepository;

    @Autowired
    public InteractionService(InteractionRepository interactionRepository, MemberRepository memberRepository, FeedRepository feedRepository){
        this.interactionRepository = interactionRepository;
        this.memberRepository = memberRepository;
        this.feedRepository = feedRepository;
    }

    public void insertInteraction(InteractionCreationRequestBody interactionCreationRequestBody){
        Member tmpMember = memberRepository.findByMemberId(interactionCreationRequestBody.getMemberId());
        Feed tmpFeed = feedRepository.findByFeedId(interactionCreationRequestBody.getFeedId());

        Interaction interaction = Interaction.builder()
                .interactionId(UUID.randomUUID().toString().replace("-", ""))
                .interactionFeed(tmpFeed) // 좋아요 누를 피드
                .interactionMember(tmpMember) // 좋아요 누른 멤버
        .build();
        interactionRepository.save(interaction);
    }
}
