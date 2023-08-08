package com.teamseven.MusicVillain.Interaction;

import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Interaction.RequestBodyForm.InteractionCreationRequestBody;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public List<Interaction> getAllInteractions(){
        return interactionRepository.findAll();
    }

    public Map<String,String> insertInteraction(InteractionCreationRequestBody interactionCreationRequestBody){
        // feed 테이블의 interaction_count도 증가시켜줘야함

        Map resultMap = new HashMap<>();
        Member tmpMember = memberRepository.findByMemberId(interactionCreationRequestBody.getMemberId());
        Feed tmpFeed = feedRepository.findByFeedId(interactionCreationRequestBody.getFeedId());
        if(tmpFeed == null || tmpMember == null)
        {
            // 해당하는 멤버나 피드가 없다면 Bad Request
            resultMap.put("result", "fail");
            return resultMap;
        }

        // 이미 해당 멤버가 Interaction 한적이 있는지 확인. 있다면 좋아요 취소로 동작
        Interaction checkInteraction = interactionRepository.findByInteractionMemberAndInteractionFeed(tmpMember, tmpFeed);

        if(checkInteraction == null)
        {
            // 좋아요로 동작
            String generatedInteractionId = UUID.randomUUID().toString().replace("-", "");
            resultMap.put("interactionId", generatedInteractionId);

            Interaction tmpInteraction = Interaction.builder()
                    .interactionId(generatedInteractionId)
                    .interactionFeed(tmpFeed) // 좋아요 누를 피드
                    .interactionMember(tmpMember) // 좋아요 누른 멤버
                    .build();
            interactionRepository.save(tmpInteraction);
            // 해당 피드 찾아서 피드 테이블의 interaction_count 증가시켜줌
            tmpFeed.setInteractionCount(tmpFeed.getInteractionCount() + 1);
            feedRepository.save(tmpFeed);
        }
        else{
            // 좋아요 취소로 동작
            // interaction 테이블에서 해당 엔트리 삭제후 feed 테이블의 interaction_count 감소시켜줌
            interactionRepository.delete(checkInteraction);
            tmpFeed.setInteractionCount(tmpFeed.getInteractionCount() -1);
            feedRepository.save(tmpFeed);
        }

        resultMap.put("result", "success");
        return resultMap;
    }
}
