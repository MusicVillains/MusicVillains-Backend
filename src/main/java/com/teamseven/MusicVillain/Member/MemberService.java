package com.teamseven.MusicVillain.Member;
import com.teamseven.MusicVillain.Dto.DataTransferObject;
import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Feed.FeedService;
import com.teamseven.MusicVillain.Interaction.InteractionRepository;
import com.teamseven.MusicVillain.Interaction.InteractionService;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Dto.RequestBody.MemberCreationRequestBody;
import com.teamseven.MusicVillain.Security.JWT.JwtManager;
import com.teamseven.MusicVillain.Security.JWT.JwtTokenRepository;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import com.teamseven.MusicVillain.Dto.Converter.DtoConverter;
import com.teamseven.MusicVillain.Dto.Converter.DtoConverterFactory;
import com.teamseven.MusicVillain.Dto.MemberDto;
import com.teamseven.MusicVillain.Utils.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FeedRepository feedRepository;
    private final InteractionRepository interactionRepository;
    private final FeedService feedService;
    private final InteractionService interactionService;
    private final JwtTokenRepository jwtTokenRepository;

    private DtoConverter dtoConverter =
            DtoConverterFactory.getConverter(Member.class, MemberDto.class);
    {
        /* ──────────────────────── For test ──────────────────────────── */
        log.trace("MemberService dtoConverter : {}", dtoConverter.getClass());

    }


    /* ──────────────────────── MEMBER CRUD ──────────────────────── */

    /**
     * 모든 멤버를 데이터베이스에서 가져옵니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @return 멤버 정보 리스트
     */
    public List<DataTransferObject> getAllMembers(){

        List<DataTransferObject> dtoList =
                dtoConverter.convertToDtoList(memberRepository.findAll());
        return dtoList;
    }

    /**
     * 멤버 ID로 멤버 정보를 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param memberId 조회할 멤버의 ID
     * @return ServiceResult 객체. 멤버 정보 또는 실패 메시지를 포함.
     */
    public ServiceResult getMemberById(String memberId) {
        // Parameter null check
        if(memberId == null) return ServiceResult.fail("Member id is null");

        // check it's valid id format
        if(!Utility.isValidUUID(memberId)) return ServiceResult.fail("Invalid member id format");

        DataTransferObject nullableDto =
                dtoConverter.convertToDto(memberRepository.findByMemberId(memberId));

        if(nullableDto == null)
            return ServiceResult.fail("Member not found");

        return ServiceResult.success(nullableDto);
    }

    /**
     * 새로운 멤버를 데이터베이스에 생성합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param memberCreationRequestBody 새 멤버 생성에 필요한 데이터
     * @return ServiceResult 객체. 생성된 멤버의 ID 또는 실패 메시지를 포함.
     */
    public ServiceResult insertMember(String userId, String userInfo, String name, String email){

        // memberRequestDto 필드 값중 하나라도 null인 것이 있는지 확인
        if(userId == null || userInfo == null || name == null || email == null){
            return ServiceResult.fail("Member field is null");
        }

        // 이미 존재하는 멤버인지 확인
        if(memberRepository.findByUserId(userId) != null){
            return ServiceResult.fail("Member already exists");
        }

        // 사용자 아이디에 특수문자가 들어가거나 숫자로 시작하는지 검사
        if (!isValidUserIdPattern(userId)) {
            return ServiceResult.fail("Invalid user id pattern");
        }
        String generatedMemberId = RandomUUIDGenerator.generate();
        // 새로운 멤버 생성
        Member member = Member.builder()
                .memberId(generatedMemberId)
                .userId(userId)
                .userInfo(bCryptPasswordEncoder.encode(userInfo))
                .name(name)
                .email(email)
                .providerType("LOCAL")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .build();
        System.out.println(member);

        memberRepository.save(member);
        return ServiceResult.success(member);

    }

    /**
     * 멤버의 닉네임을 수정합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param memberId 닉네임을 수정할 멤버의 ID
     * @param nickname 멤버에게 부여할 새 닉네임
     * @return ServiceResult 객체. 성공 또는 실패 메시지를 포함.
     */
    public ServiceResult modifyMemberNickname(String memberId, String nickname) {
        // Parameter null check
        if(memberId == null || nickname == null) {
            return ServiceResult.fail("Bad Request, Given memberId or nickname is null");
        }

        Member nullableMember = memberRepository.findByMemberId(memberId);

        // check if Member exists in DB
        if (nullableMember == null) return ServiceResult.fail("Member not found");

        // check if it's valid nickname format
        if(!this.isValidUserIdPattern(nickname)){
            return ServiceResult.fail("Invalid nickname format");
        }

        // check if nickname(to modify) is same with current nickname
        if(nullableMember.getName().equals(nickname)){
            return ServiceResult.fail("Nickname is same with current nickname");
        }



        nullableMember.setName(nickname);
        memberRepository.save(nullableMember);

        return ServiceResult.success("nickname changed");
    }

    /**
     * 멤버 ID로 멤버를 삭제합니다.
     * 이 작업은 해당 멤버와 관련된 모든 인터랙션과 피드도 삭제합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param memberId 삭제할 멤버의 ID
     * @return ServiceResult 객체. 삭제된 멤버의 ID 또는 실패 메시지를 포함.
     */
    @Transactional
    public ServiceResult deleteMemberByMemberId(String memberId){
        // Parameter null check
        if(memberId == null)
            return ServiceResult.fail("Member id is null");

        if(!Utility.isValidUUID(memberId))
            return ServiceResult.fail("Invalid member id format");

        // Check Member Exist in DB
        if(!isExistMember(memberId)){
            log.trace("Member does not exist");
            return ServiceResult.fail("member does not exist");
        }

        // 멤버가 다른 사용자에게 인터랙션한 경우 해당 인터랙션 삭제
        interactionRepository.deleteByInteractionMemberMemberId(memberId);

        // 멤버가 보유한 피드를 리스트로 받아온 후 해당 피드와 피드와 관련된 인터렉션 삭제

        // 멤버가 보유한 피드 리스트 받아오기
        List<Feed> memberFeedList = feedRepository.findAllByOwnerMemberId(memberId);

        for (Feed f : memberFeedList){
            // 각 피드 및 피드와 관련된 인터렉션 삭제
            interactionRepository.deleteByInteractionFeedFeedId(f.getFeedId());

            /* 멤버의 피드 삭제, 레코드는 CASCADE 옵션으로 피드 삭제시 같이 삭제됨 */
            feedRepository.deleteByFeedId(f.getFeedId());
        }

        // 멤버 삭제
        memberRepository.deleteByMemberId(memberId);

        // 관련 토큰 삭제
        jwtTokenRepository.deleteAllByOwnerIdAndType(memberId, JwtManager.TYPE_ACCESS_TOKEN());
        jwtTokenRepository.deleteAllByOwnerIdAndType(memberId, JwtManager.TYPE_REFRESH_TOKEN());

        return ServiceResult.success(memberId);
    }

    /* ──────────────────────── UTILS ──────────────────────── */
    /**
     * 멤버가 데이터베이스에 존재하는지 확인합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param userId 확인할 멤버의 사용자 ID
     * @return 멤버가 존재하면 true, 그렇지 않으면 false
     */
    private boolean isExistMember(String userId) {
        return memberRepository.findByMemberId(userId) != null;
    }

    /**
     * 사용자 ID 패턴이 유효한지 검사합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param userId 검사할 사용자 ID
     * @return 사용자 ID가 유효하면 true, 그렇지 않으면 false
     */
    private boolean isValidUserIdPattern(String userId) {
        // 특수문자 또는 숫자로 시작하는지 검사하는 정규표현식
        String pattern = "^[a-zA-Z][a-zA-Z0-9]*$";
        return userId.matches(pattern);
    }


}
