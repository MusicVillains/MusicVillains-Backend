package com.teamseven.MusicVillain.Member;

import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Feed.FeedService;
import com.teamseven.MusicVillain.Interaction.InteractionRepository;
import com.teamseven.MusicVillain.Interaction.InteractionService;
import com.teamseven.MusicVillain.ServiceResult;
import com.teamseven.MusicVillain.Status;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FeedRepository feedRepository;
    private final InteractionRepository interactionRepository;
    private final FeedService feedService;
    private final InteractionService interactionService;


    @Autowired
    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                         FeedRepository feedRepository, InteractionRepository interactionRepository,
                         FeedService feedService, InteractionService interactionService){
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.feedRepository = feedRepository;
        this.interactionRepository = interactionRepository;
        this.feedService = feedService;
        this.interactionService = interactionService;
    }


    /* ──────────────────────── MEMBER CRUD ──────────────────────── */
    public List<Member> getAllMembers(){
        return memberRepository.findAll();
    }
    public ServiceResult getMemberById(String memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        if(member == null) return ServiceResult.fail("Member not found");

        return ServiceResult.success(member);
    }
    public ServiceResult insertMember(MemberCreationRequestBody memberCreationRequestBody){
        // 이미 존재하는 멤버인지 확인
        if(memberRepository.findByUserId(memberCreationRequestBody.getUserId()) != null){
            return ServiceResult.fail("Member already exists");
        }

        // memberRequestDto 필드 값중 하나라도 null인 것이 있는지 확인
        if(memberCreationRequestBody.hasNullField()){
            return ServiceResult.fail("Member field is null");
        }

        // 사용자 아이디에 특수문자가 들어가거나 숫자로 시작하는지 검사
        if (!isValidUserIdPattern(memberCreationRequestBody.getUserId())) {
            return ServiceResult.fail("Invalid user id pattern");
        }
        String generatedMemberId = UUID.randomUUID().toString().replace("-", "");
        // 새로운 멤버 생성
        Member member = Member.builder()
                .memberId(generatedMemberId)
                .userId(memberCreationRequestBody.getUserId())
                .userInfo(bCryptPasswordEncoder.encode(memberCreationRequestBody.getUserInfo()))
                .name(memberCreationRequestBody.getName())
                .email(memberCreationRequestBody.getEmail())
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();
        System.out.println(member);

        memberRepository.save(member);
        return ServiceResult.success(generatedMemberId);

    }
    public ServiceResult modifyMemberNickname(String memberId, String nickname) {
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) return ServiceResult.fail("Member not found");

        member.setName(nickname);
        memberRepository.save(member);

        return ServiceResult.success("nickname changed");
    }

    @Transactional
    public ServiceResult deleteMemberByMemberId(String memberId){
        // 멤버가 존재하는지 확인
        if(isExistMember(memberId) == false){
            return ServiceResult.fail("member does not exist");
        }

        // 멤버가 다른 사용자에게 인터랙션한 경우 해당 인터랙션 삭제
        interactionRepository.deleteByInteractionMemberMemberId(memberId);

        // 멤버가 보유한 피드를 리스트로 받아온 후 해당 피드와 피드와 관련된 인터렉션 삭제
        List<Feed> memberFeedList = feedRepository.findAllByOwnerMemberId(memberId);

        for (Feed f : memberFeedList){
            // 각 피드 및 피드와 관련된 인터렉션 삭제
            interactionRepository.deleteByInteractionFeedFeedId(f.getFeedId());

            /* 멤버의 피드 삭제, 레코드는 CASCADE 옵션으로 피드 삭제시 같이 삭제됨 */
            feedRepository.deleteByFeedId(f.getFeedId());
        }

        // 멤버 삭제
        memberRepository.deleteByMemberId(memberId);

        return ServiceResult.success(memberId);
    }

    /* ──────────────────────── UTILS ──────────────────────── */

    private boolean isExistMember(String userId) {
        return memberRepository.findByMemberId(userId) != null;
    }

    // 사용자 아이디 유효성 검사 메서드
    private boolean isValidUserIdPattern(String userId) {
        // 특수문자 또는 숫자로 시작하는지 검사하는 정규표현식
        String pattern = "^[a-zA-Z][a-zA-Z0-9]*$";
        return userId.matches(pattern);
    }


}
