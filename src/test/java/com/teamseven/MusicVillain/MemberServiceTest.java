package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Dto.Converter.MemberDtoConverter;
import com.teamseven.MusicVillain.Dto.MemberDto;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Member.MemberService;
import com.teamseven.MusicVillain.Security.MemberRole;
import com.teamseven.MusicVillain.Security.OAuth.OAuth2ProviderType;
import com.teamseven.MusicVillain.Utils.RandomNicknameGenerator;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDateTime;


@SpringBootTest
@RequiredArgsConstructor
@Slf4j
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("MemberService 통합 테스트")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL) // for Constructor Injection
public class MemberServiceTest {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    Member testMember;
    String testMemberAccessToken;


    @Nested
    @Order(1)
    @DisplayName("CREATE 테스트")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreateMemberTest{

        @Test
        @Order(1)
        @Transactional
        @DisplayName("회원 생성 테스트")
        void createMember(){
            // given

            String userId  = "mockUserId";
            String name = RandomNicknameGenerator.generate();
            String email = "mock@mail.com";

            // when
            ServiceResult serviceResult = memberService.insertMember(userId, "-", name, email);

            // then
            Member createdMember = memberRepository.findByMemberId(
                    ((MemberDto)serviceResult.getData()).memberId);

            Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
            Assertions.assertNotNull(createdMember);
            Assertions.assertEquals(userId, createdMember.userId);
            Assertions.assertEquals(name, createdMember.name);
            Assertions.assertEquals(email, createdMember.email);

        }
    }

    @Nested
    @Order(2)
    @DisplayName("READ 테스트")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ReadMemberTest{

        @Test
        @Order(1)
        @DisplayName("회원 조회 테스트")
        void readMember(){
            // given
            MemberDtoConverter dtoConverter = new MemberDtoConverter();

            // when
            ServiceResult serviceResult = memberService.getAllMembers();

            // then
            Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
        }
    }

    @Nested
    @Order(3)
    @DisplayName("UPDATE 테스트")
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class UpdateMemberNicknameTest {

        /* ──────────────────────── Setup ────────────────────── */
        @BeforeEach
        // 각 테스트 메소드가 호출될 때마다 매번 실행
        @DisplayName("테스트 멤버 생성 및 로그인 처리")
        void setUpTestMemberAndIssueJwt(){
            log.info("* 테스트 멤버 생성 및 로그인 처리 ");
            // create Member
            String generatedMemberId = RandomUUIDGenerator.generate();
            String generatedUserId = "TEST_USER";
            String generatedName = "testUser";

            // check if generatedName is already used
            while(memberRepository.findByName(generatedName) != null){
                generatedName = RandomNicknameGenerator.generate();
            }

            Member member = Member.builder()
                    .memberId(generatedMemberId)
                    .providerType(OAuth2ProviderType.KAKAO)
                    .userId(generatedUserId)
                    .name(generatedName) // user nickname
                    .userInfo("-")
                    .email("-")
                    .role(MemberRole.USER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            memberRepository.save(member);

            testMember = member;
            log.info("* Member registered successfully\n" +
                            "\t- memberId: {}\n" +
                            "\t- userId: {}\n" +
                            "\t- name: {}\n"+
                            "\t- role: {}\n"+
                            "\t- providerType: {}"
                    , testMember.getMemberId(), testMember.getUserId(), testMember.getName(), testMember.getRole(), testMember.getProviderType());
        }

        @AfterEach
        // 각 테스트 메소드가 호출될 때마다 매번 실행
        @DisplayName("테스트 멤버 삭제")
        void deleteTestMember(){
            log.info("* 테스트 멤버 삭제 ");
            memberService.deleteMemberByMemberId(testMember.getMemberId());
        }

        /////////////////////////// Setup ///////////////////////////



        /* ─────────────────── 회원정보 수정 테스트 ────────────────── */

        @Test
        @Order(1)
        @DisplayName("회원 닉네임 수정 - 유효하지 않은 닉네임")
        void memberNicknameModifyTestWithInValidToken() {
            String originalNickname = testMember.getName();
            String nicknameToModify = originalNickname + "_modified";

            memberService.modifyMemberNickname(testMember.getMemberId(), nicknameToModify);

            // then: 닉네임이 변경되지 않아야 한다.
            Assertions.assertEquals(originalNickname,
                    memberRepository.findByMemberId(testMember.getMemberId()).getName());

            log.info("\n" +
                    "\t* expected: {}\n" +
                    "\t* actual: {}", "testUser",memberRepository.findByMemberId(testMember.getMemberId()).getName());
        }


//        @Test
//        @Order(2)
//        @DisplayName("회원 닉네임 수정 - 유효한 닉네임")
//        void memberNicknameModifyTestWithValidToken() {
//
//            String originalNickname = testMember.getName();
//            log.info("originalNickname: {}", originalNickname);
//            String nicknameToModify = originalNickname+"Modified";
//            log.info("nicknameToModify: {}", nicknameToModify);
//
//            // when
//            ServiceResult serviceResult = memberService.modifyMemberNickname(testMember.getMemberId(), nicknameToModify);
//
//            // then
//            Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
//            Assertions.assertEquals(nicknameToModify,
//                    memberRepository.findByMemberId(testMember.getMemberId()).getName());
//
//            log.info("\n" +
//                    "\t* expected: {}\n" +
//                    "\t* actual: {}", nicknameToModify ,memberRepository.findByMemberId(testMember.getMemberId()).getName());
//
//
//        }

        @Test
        @Order(2)
        @DisplayName("회원 닉네임 수정 - 유효한 닉네임")
        @Transactional
        void memberNicknameModifyTestWithValidToken() {
            // Given
            String originalNickname = testMember.getName();
            log.info("originalNickname: {}", originalNickname);
            String nicknameToModify = originalNickname + "Modified";
            log.info("nicknameToModify: {}", nicknameToModify);

            // When
            ServiceResult serviceResult = memberService.modifyMemberNickname(testMember.getMemberId(), nicknameToModify);

            // Then
            Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
            Member updatedMember = memberRepository.findByMemberId(testMember.getMemberId());
            Assertions.assertNotNull(updatedMember, "Updated member should not be null");
            Assertions.assertEquals(nicknameToModify, updatedMember.getName(), "Nicknames should match");
            Assertions.assertEquals("Nickname changed successfully", serviceResult.getData(), "Success message should match");
            log.info("\n" +
                    "\t* expected: {}\n" +
                    "\t* actual: {}", nicknameToModify ,memberRepository.findByMemberId(testMember.getMemberId()).getName());
        }
        /////////////////////////// 회원정보 수정 테스트 ///////////////////////////


    }

    /* ─────────────────────────── 회원 삭제 테스트 ───────────────────────── */
    @Nested
    @Order(4)
    @DisplayName("DELETE 테스트")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class deleteMemberTest{
        String memberId;
        String userId;

        @BeforeEach
        @DisplayName("삭제할 멤버 생성")
        void setUp(){
            memberId = RandomUUIDGenerator.generate();
            userId = "test_user";

            Member member = Member.builder()
                    .memberId(memberId)
                    .userId("test")
                    .build();

            memberRepository.save(member);
            Assertions.assertNotNull(memberRepository.findByMemberId(memberId));
        }

        @Test
        @Transactional
        @DisplayName("회원 식별자로 회원 삭제")
        void deleteMemberByMemberIdTest(){

            // given
            log.info("memberId: {}", memberId);

            // when
            memberService.deleteMemberByMemberId(memberId);

            // then
            Assertions.assertNull(memberRepository.findByMemberId(memberId));

        }

    }


}