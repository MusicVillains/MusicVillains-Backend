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


    /**
     * 모든 인터랙션을 반환합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @return 모든 Interaction 객체의 List
     */
    public List<Interaction> getAllInteractions(){
        return interactionRepository.findAll();
    }

    /**
     * 새로운 인터랙션을 삽입하거나 이미 존재하는 인터랙션을 삭제합니다.
     * (좋아요/좋아요 취소 기능)
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param interactionCreationRequestBody Interaction 생성에 필요한 데이터
     * @return ServiceResult 객체. 성공 또는 실패 메시지를 포함.
     */
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

    /**
     * 특정 인터랙션 ID에 해당하는 인터랙션을 삭제합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param interactionId 삭제할 인터랙션의 ID
     */
    @Transactional
    public void deleteInteractionByInteractionId(String interactionId){
        Interaction targetInteraction = interactionRepository.findByInteractionId(interactionId);

        // 해당 feed 의 interactionCount 를 1 감소시켜줌
        Feed tmpFeed = feedRepository.findByFeedId(targetInteraction.getInteractionFeed().getFeedId());
        feedRepository.save(tmpFeed);

        interactionRepository.deleteByInteractionId(interactionId);
    }

    /**
     * 특정 회원 ID가 가진 모든 인터랙션을 삭제합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param memberId 인터랙션을 삭제할 회원의 ID
     */
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

    /**
     * 특정 피드 ID에 연관된 모든 인터랙션을 삭제합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param feedId 인터랙션을 삭제할 피드의 ID
     */
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

    /**
     * 특정 피드 ID에 연관된 인터랙션의 개수를 반환합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param feedId 인터랙션 개수를 알고 싶은 피드의 ID
     * @return ServiceResult 객체. 성공시 피드에 연관된 인터랙션의 개수를 포함.
     */
    public ServiceResult getInteractionCountByFeedId(String feedId) {

        return ServiceResult.success(interactionRepository.countByInteractionFeedFeedId(feedId));
    }
}
