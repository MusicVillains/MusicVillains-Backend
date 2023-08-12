package com.teamseven.MusicVillain.Interaction;

import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Dto.RequestBody.InteractionCreationRequestBody;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Notification.NotificaitonRepository;
import com.teamseven.MusicVillain.Notification.Notification;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final MemberRepository memberRepository;
    private final FeedRepository feedRepository;
    private final NotificaitonRepository notificaitonRepository;


    @Autowired
    public InteractionService(InteractionRepository interactionRepository,
                              MemberRepository memberRepository, FeedRepository feedRepository,
                              NotificaitonRepository notificaitonRepository){

        this.interactionRepository = interactionRepository;
        this.memberRepository = memberRepository;
        this.feedRepository = feedRepository;
        this.notificaitonRepository = notificaitonRepository;
    }

    public List<Interaction> getAllInteractions(){
        return interactionRepository.findAll();
    }

    @Transactional
    public ServiceResult insertInteraction(InteractionCreationRequestBody interactionCreationRequestBody){
        // feed 테이블의 interaction_count도 증가시켜줘야함

        Member tmpMember = memberRepository.findByMemberId(interactionCreationRequestBody.getMemberId());
        Feed tmpFeed = feedRepository.findByFeedId(interactionCreationRequestBody.getFeedId());
        // 해당하는 멤버나 피드가 존재하는지 체크
        if(tmpFeed == null || tmpMember == null)
            return ServiceResult.fail("Member or Feed not found");

        // 이미 해당 멤버가 Interaction 한적이 있는지 확인. 있다면 좋아요 취소로 동작
        Interaction checkInteraction = interactionRepository.findByInteractionMemberAndInteractionFeed(tmpMember, tmpFeed);

        if(checkInteraction == null)
        {
            // 좋아요로 동작
            String generatedInteractionId = UUID.randomUUID().toString().replace("-", "");

            Interaction tmpInteraction = Interaction.builder()
                    .interactionId(generatedInteractionId)
                    .interactionFeed(tmpFeed) // 좋아요 누를 피드
                    .interactionMember(tmpMember) // 좋아요 누른 멤버
                    .build();
            interactionRepository.save(tmpInteraction);

            Notification tmpNotification =
                    Notification.builder()
                            .notificationId(RandomUUIDGenerator.generate())
                            .interaction(tmpInteraction)
                            .owner(tmpFeed.getOwner())
                            .ownerRead(Notification.NOTIFICATION_UNREAD)
                            .createdAt(LocalDateTime.now())
                            .build();
            notificaitonRepository.save(tmpNotification);

            return ServiceResult.of(ServiceResult.SUCCESS, "Interaction created", generatedInteractionId);
        }
        else {
            // 관련 notificaiton 삭제
            notificaitonRepository.deleteByInteraction(checkInteraction);
            // 인터렉션 삭제하여 좋아요 취소로 동작하도록 함.
            // 좋아요 취소로 동작
            interactionRepository.delete(checkInteraction);
            return ServiceResult.of(ServiceResult.SUCCESS, "Interaction deleted", null);
        }
    }
    @Transactional
    public void deleteInteractionByInteractionId(String interactionId){
        Interaction targetInteraction = interactionRepository.findByInteractionId(interactionId);

        // 해당 feed 의 interactionCount 를 1 감소시켜줌
        Feed tmpFeed = feedRepository.findByFeedId(targetInteraction.getInteractionFeed().getFeedId());
        feedRepository.save(tmpFeed);

        interactionRepository.deleteByInteractionId(interactionId);
    }

    @Transactional
    public void deleteInteractionByMemberId(String memberId){
        // deleteByInteractionMemberMemberId 로 한번에 삭제해버리면 안됨.
        // 각 인터렉션 아이디 얻어온 후, 인터렉션 아이디 하나 하나 삭제해주면서 해당 피드의 interactionCount 1 감소시켜줘야함

        List<Interaction> interactions = interactionRepository.findAll(); // 모든 Interaction 엔티티 가져오기
        List<String> interactionIdListToDelete = interactions.stream()
                .filter(interaction -> interaction.getInteractionMember().getMemberId().equals(memberId)) // memberId에 해당하는 Interaction 필터링
                .map(Interaction::getInteractionId) // Interaction의 interaction_id 추출
                .collect(Collectors.toList()); // 리스트에 추가

        for (String interactionId : interactionIdListToDelete) {
           this.deleteInteractionByInteractionId(interactionId);
        }

    }

    @Transactional
    public void deleteInterationsByFeedId(String feedId) {
        List<Interaction> interactionList = interactionRepository.findByInteractionFeedFeedId(feedId);

        //  notification 삭제
        for(Interaction i: interactionList){
            notificaitonRepository.deleteByInteraction(i);
        }

        // interaction 삭제
        interactionRepository.deleteByInteractionFeedFeedId(feedId);

    }

    public ServiceResult getInteractionCountByFeedId(String feedId) {

        return ServiceResult.success(interactionRepository.countByInteractionFeedFeedId(feedId));
    }
}
