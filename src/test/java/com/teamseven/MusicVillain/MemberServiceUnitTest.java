package com.teamseven.MusicVillain;


import com.sun.source.tree.ModuleTree;
import com.teamseven.MusicVillain.Dto.DataTransferObject;
import com.teamseven.MusicVillain.Dto.MemberDto;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Interaction.InteractionRepository;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Member.MemberService;
import com.teamseven.MusicVillain.Security.JWT.JwtTokenRepository;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestConstructor;
import java.util.List;

@SpringBootTest
@DisplayName("MemberService 단위 테스트")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Slf4j
public class MemberServiceUnitTest {

    @Mock
    MemberRepository mockMemberRepository;

    @Mock
    InteractionRepository mockInteractionRepository;

    @Mock
    FeedRepository mockFeedRepository;

    @Mock
    JwtTokenRepository mockJwtTokenRepository;

    @Mock
    BCryptPasswordEncoder mockBCryptPasswordEncoder;
    String mockEncryptedPassword = "mockEncryptedPassword";

    @InjectMocks
    MemberService memberService;



    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Create 테스트")
    class CreateTest{
        @BeforeEach
        void setUp(){

            // count 초기화
            Mockito.reset(mockBCryptPasswordEncoder);
            Mockito.reset(mockMemberRepository);

            // 리턴값 설정
            Mockito.when(mockBCryptPasswordEncoder.encode(Mockito.any()))
                    .thenReturn(mockEncryptedPassword);

            Mockito.when(mockMemberRepository.save(Mockito.any())).thenReturn(null);
        }

        @Test
        @Order(1)
        @DisplayName("정상적인 요청의 경우 동일한 정보를 가진 멤버가 생성되어야 한다.")
        void createMemberTest(){
            //given
            String mockUserId = "mockUserId";
            String mockUserInfo = "mockUserInfo";
            String mockName = "mockName";
            String mockEmail = "mockEmail";

            //when
            ServiceResult serviceResult = memberService.insertMember(mockUserId, mockUserInfo, mockName, mockEmail);

            //then
            Member createdMember = (Member)serviceResult.getData();

            log.info("[expect / actual]\n" +
                    "- Mockito.verify(mockBCryptPasswordEncoder..).encode(..): " + "1" + " / " + Mockito.mockingDetails(mockBCryptPasswordEncoder).getInvocations().size() + "\n" +
                    "- Mockito.verify(mockMemberRepository..).save(..): " + "1" + " / " + Mockito.mockingDetails(mockMemberRepository).getInvocations().size() + "\n" +
                    "- serviceResult.getResult(): " + ServiceResult.SUCCESS + " / " + serviceResult.getResult() + "\n" +
                    "- createdMember.getUserId(): " + mockUserId + " / " + createdMember.getUserId() + "\n" +
                    "- createdMember.getName(): " + mockName + " / " + createdMember.getName() + "\n" +
                    "- createdMember.getEmail(): " + mockEmail + " / " + createdMember.getEmail() + "\n" +
                    "- createdMember.getUserInfo(): " + mockEncryptedPassword + " / " + createdMember.getUserInfo() + "\n"
            );

            Mockito.verify(mockBCryptPasswordEncoder, Mockito.times(1))
                    .encode(Mockito.any());
            Mockito.verify(mockMemberRepository, Mockito.times(1))
                    .save(Mockito.any(Member.class));

            Assertions.assertEquals(serviceResult.getResult(), ServiceResult.SUCCESS);
            Assertions.assertTrue(createdMember.getUserId().equals(mockUserId));
            Assertions.assertTrue(createdMember.getName().equals(mockName));
            Assertions.assertTrue(createdMember.getEmail().equals(mockEmail));
            Assertions.assertTrue(createdMember.getUserInfo().equals(mockEncryptedPassword));

        }

        @Test
        @Order(2)
        @DisplayName("필요한 인자가 누락된 경우 생성되지 않아야 한다. - `userId` 누락")
        void CreateMemberTestWithUserIdNull(){
            //given
            String mockUserId = null;
            String mockUserInfo = "mockUserInfo";
            String mockName = "mockName";
            String mockEmail = "mockEmail";
            //when
            ServiceResult serviceResult = memberService.insertMember(mockUserId, mockUserInfo, mockName, mockEmail);

            //then

            log.info("[expect / actual]\n" +
                    "- Mockito.verify(mockBCryptPasswordEncoder..).encode(..): " + "0" + " / " + Mockito.mockingDetails(mockBCryptPasswordEncoder).getInvocations().size() + "\n" +
                    "- Mockito.verify(mockMemberRepository..).save(..): " + "0" + " / " + Mockito.mockingDetails(mockMemberRepository).getInvocations().size() + "\n" +
                    "- serviceResult.getResult(): " + ServiceResult.FAIL + " / " + serviceResult.getResult());

            Mockito.verify(mockBCryptPasswordEncoder, Mockito.times(0))
                    .encode(Mockito.any());
            Mockito.verify(mockMemberRepository, Mockito.times(0))
                    .save(Mockito.any(Member.class));
            Assertions.assertEquals(serviceResult.getResult(), ServiceResult.FAIL);
        }


        @Test
        @Order(2)
        @DisplayName("필요한 인자가 누락된 경우 생성되지 않아야 한다. - `userInfo` 누락")
        void CreateMemberTestWithUserInfoNull(){
            //given
            String mockUserId = "mockUserId";
            String mockUserInfo = null;
            String mockName = "mockName";
            String mockEmail = "mockEmail";
            //when
            ServiceResult serviceResult = memberService.insertMember(mockUserId, mockUserInfo, mockName, mockEmail);

            //then
            log.info("[expect / actual]\n" +
                    "- Mockito.verify(mockBCryptPasswordEncoder..).encode(..): " + "0" + " / " + Mockito.mockingDetails(mockBCryptPasswordEncoder).getInvocations().size() + "\n" +
                    "- Mockito.verify(mockMemberRepository..).save(..): " + "0" + " / " + Mockito.mockingDetails(mockMemberRepository).getInvocations().size() + "\n" +
                    "- serviceResult.getResult(): " + ServiceResult.FAIL + " / " + serviceResult.getResult());

            Mockito.verify(mockBCryptPasswordEncoder, Mockito.times(0))
                    .encode(Mockito.any());
            Mockito.verify(mockMemberRepository, Mockito.times(0))
                    .save(Mockito.any(Member.class));
            Assertions.assertEquals(serviceResult.getResult(), ServiceResult.FAIL);
        }


        @Test
        @Order(3)
        @DisplayName("필요한 인자가 누락된 경우 생성되지 않아야 한다. - `name` 누락")
        void CreateMemberTestWithNameNull(){
            //given
            String mockUserId = "mockUserId";
            String mockUserInfo = "mockUserInfo";
            String mockName = null;
            String mockEmail = "mockEmail";
            //when
            ServiceResult serviceResult = memberService.insertMember(mockUserId, mockUserInfo, mockName, mockEmail);

            //then
            log.info("[expect / actual]\n" +
                    "- Mockito.verify(mockBCryptPasswordEncoder..).encode(..): " + "0" + " / " + Mockito.mockingDetails(mockBCryptPasswordEncoder).getInvocations().size() + "\n" +
                    "- Mockito.verify(mockMemberRepository..).save(..): " + "0" + " / " + Mockito.mockingDetails(mockMemberRepository).getInvocations().size() + "\n" +
                    "- serviceResult.getResult(): " + ServiceResult.FAIL + " / " + serviceResult.getResult());
            Mockito.verify(mockBCryptPasswordEncoder, Mockito.times(0))
                    .encode(Mockito.any());
            Mockito.verify(mockMemberRepository, Mockito.times(0))
                    .save(Mockito.any(Member.class));
            Assertions.assertEquals(serviceResult.getResult(), ServiceResult.FAIL);
        }

        @Test
        @Order(4)
        @DisplayName("필요한 인자가 누락된 경우 생성되지 않아야 한다. - `email` 누락")
        void CreateMemberTestWithEmailNull(){
            //given
            String mockUserId = "mockUserId";
            String mockUserInfo = "mockUserInfo";
            String mockName = "mockName";
            String mockEmail = null;
            //when
            ServiceResult serviceResult = memberService.insertMember(mockUserId, mockUserInfo, mockName, mockEmail);

            //then
            log.info("[expect / actual]\n" +
                    "- Mockito.verify(mockBCryptPasswordEncoder..).encode(..): " + "0" + " / " + Mockito.mockingDetails(mockBCryptPasswordEncoder).getInvocations().size() + "\n" +
                    "- Mockito.verify(mockMemberRepository..).save(..): " + "0" + " / " + Mockito.mockingDetails(mockMemberRepository).getInvocations().size() + "\n" +
                    "- serviceResult.getResult(): " + ServiceResult.FAIL + " / " + serviceResult.getResult());
            Mockito.verify(mockBCryptPasswordEncoder, Mockito.times(0))
                    .encode(Mockito.any());
            Mockito.verify(mockMemberRepository, Mockito.times(0))
                    .save(Mockito.any(Member.class));
            Assertions.assertEquals(serviceResult.getResult(), ServiceResult.FAIL);
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Read 테스트")
    class ReadTest{
        int mockMemberListSize;
        @BeforeEach
        void setUp(){
            Mockito.reset(mockMemberRepository);

            String memberId1 = RandomUUIDGenerator.generate();
            String memberId2 = RandomUUIDGenerator.generate();
            String memberId3 = RandomUUIDGenerator.generate();

            List<Member> mockMemberList = List.of(
                    Member.builder().memberId(memberId1).userId("mockUserId1").email("mockEmail1").userInfo("mockUserInfo1").name("mockName1").build(),
                    Member.builder().memberId(memberId2).userId("mockUserId2").email("mockEmail2").userInfo("mockUserInfo2").name("mockName2").build(),
                    Member.builder().memberId(memberId3).userId("mockUserId3").email("mockEmail3").userInfo("mockUserInfo3").name("mockName3").build()
            );
            mockMemberListSize = mockMemberList.size();

            Mockito.when(mockMemberRepository.findAll()).thenReturn(mockMemberList);
        }


        @Test
        @Order(1)
        @DisplayName("존재하는 모든 멤버를 조회할 수 있다.")
        void getAllMembersTest(){
            // given

            // when
            List<DataTransferObject> dataTransferObjectList = memberService.getAllMembers();

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(1)).findAll();
            Assertions.assertEquals(mockMemberListSize, dataTransferObjectList.size());
        }

        @Test
        @DisplayName("`memberId`로 조회 - 유효한 멤버 식별자로 특정 멤버의 정보를 조회할 수 있다.")
        void getMemberByMemberIdTest(){
            // given
            String mockMemberId = RandomUUIDGenerator.generate();
            String mockUserId = "mockUserId";
            String mockEmail = "mockEmail";
            String mockUserInfo = "mockUserInfo";
            String mockName = "mockName";

            Member mockMember = Member.builder()
                            .memberId(mockMemberId)
                            .userId(mockUserId)
                            .email(mockEmail)
                            .userInfo(mockUserInfo)
                            .name(mockName)
                            .build();

            Mockito.when(mockMemberRepository.findByMemberId(mockMemberId))
                    .thenReturn(mockMember);

            // when
            ServiceResult serviceResult = memberService.getMemberById(mockMemberId);

            // then
            MemberDto memberDto = (MemberDto)serviceResult.getData();

            Mockito.verify(mockMemberRepository, Mockito.times(1)).findByMemberId(Mockito.anyString());
            Assertions.assertEquals(ServiceResult.SUCCESS,serviceResult.getResult());
            Assertions.assertEquals(mockMemberId,memberDto.getMemberId());
            Assertions.assertEquals(mockUserId,memberDto.getUserId());
            Assertions.assertEquals(mockName,memberDto.getName());

        }

        @Test
        @DisplayName("`memberId`로 조회 - 존재하지 않는 멤버 식별자의 경우 조회할 수 없다.")
        void getMemberByMemberIdTestWithNotExistsMemberId(){
            // given
            String mockMemberId = RandomUUIDGenerator.generate();
            String mockUserId = "mockUserId";
            String mockEmail = "mockEmail";
            String mockUserInfo = "mockUserInfo";
            String mockName = "mockName";

            Member mockMember = Member.builder()
                    .memberId(mockMemberId)
                    .userId(mockUserId)
                    .email(mockEmail)
                    .userInfo(mockUserInfo)
                    .name(mockName)
                    .build();

            // 존재하지 않는 멤버라고 가정하므로 null을 리턴
            Mockito.when(mockMemberRepository.findByMemberId(mockMemberId))
                    .thenReturn(null);

            // when
            ServiceResult serviceResult = memberService.getMemberById(mockMemberId);

            // then
            MemberDto memberDto = (MemberDto)serviceResult.getData();

            Mockito.verify(mockMemberRepository, Mockito.times(1)).findByMemberId(Mockito.anyString());
            Assertions.assertEquals(ServiceResult.FAIL,serviceResult.getResult());

        }

        @Test
        @DisplayName("`memberId`로 조회 - 멤버 식별자가 누락된 경우 조회할 수 없다.")
        void getMemberByMemberIdTestWithMemberIdNull(){
            // given
            String memberId = null;

            // when
            ServiceResult serviceResult = memberService.getMemberById(memberId);

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(0))
                    .findByMemberId(Mockito.anyString());
            Assertions.assertEquals(ServiceResult.FAIL,serviceResult.getResult());

        }

        /*TODO: 기능 구현 예정*/
        @Test
        @DisplayName("`memberId`로 조회 - 멤버 식별자가 유효하지 않은 형식일 경우 조회할 수 없다.")
        void getMemberByMemberIdTestWithInvalidFormatMemberId(){
            // given
            String invalidFormatMemberId = "invalidFormatMemberId";

            // when
            ServiceResult serviceResult = memberService.getMemberById(invalidFormatMemberId);

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(0))
                    .findByMemberId(Mockito.anyString());

            Assertions.assertEquals(ServiceResult.FAIL,serviceResult.getResult());
        }

    }

    @Nested
    @DisplayName("Update 테스트")
    class UpdateTest{
        @Test
        @DisplayName("`닉네임 수정` - 정상적인 요청의 경우 닉네임이 수정되어야 한다.")
        void updateNicknameTest(){
            // given
            String originalNickname = "originalNickname";
            String nicknameToModify = "nicknameModified";
            String memberId = RandomUUIDGenerator.generate();

            Mockito.when(mockMemberRepository.findByMemberId(memberId))
                    .thenReturn(Member.builder().memberId(memberId).name(originalNickname).build());

            Mockito.when(mockMemberRepository.save(Mockito.any(Member.class)))
                    .thenReturn(null);

            // when
            ServiceResult serviceResult = memberService.modifyMemberNickname(memberId, nicknameToModify);

            // then
            Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
        }

        @Test
        @DisplayName("`닉네임 수정` - 수정하고자 하는 닉네임과 현재 사용중인 닉네임인 경우 별도로 수정하지 않는다.")
        void modifyNicknameTestWithSameNickname(){
            // given
            String originalNickname = "originalNickname";
            String nicknameToModify = originalNickname;
            String memberId = RandomUUIDGenerator.generate();

            Mockito.when(mockMemberRepository.findByMemberId(memberId))
                    .thenReturn(Member.builder().memberId(memberId).name(originalNickname).build());

            Mockito.when(mockMemberRepository.save(Mockito.any(Member.class)))
                    .thenReturn(null);

            // when
            ServiceResult serviceResult = memberService.modifyMemberNickname(memberId, nicknameToModify);
            log.info("{}",serviceResult.getMessage());

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(1))
                    .findByMemberId(Mockito.anyString());

            Mockito.verify(mockMemberRepository, Mockito.times(0))
                    .save(Mockito.any(Member.class));

            Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
        }

        @Test
        @DisplayName("`닉네임 수정` - 수정하고자 하는 닉네임이 허용되지 않는 형식인 경우 수정되지 않아야 한다.")
        void modifyNicknameTestWithInvalidNicknameFormat(){
            // given
            String originalNickname = "originalNickname";
            String nicknameToModify = "1invalidFormatNickname";
            String memberId = RandomUUIDGenerator.generate();

            Mockito.when(mockMemberRepository.findByMemberId(memberId))
                    .thenReturn(Member.builder().memberId(memberId).name(originalNickname).build());

            Mockito.when(mockMemberRepository.save(Mockito.any(Member.class)))
                    .thenReturn(null);

            // when
            ServiceResult serviceResult = memberService.modifyMemberNickname(memberId, nicknameToModify);
            log.info("{}",serviceResult.getMessage());

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(1))
                    .findByMemberId(Mockito.anyString());

            Mockito.verify(mockMemberRepository, Mockito.times(0))
                    .save(Mockito.any(Member.class));

            Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
        }
    }

    @Nested
    @DisplayName("Delete 테스트")
    class DeleteTest{
        @Test
        @DisplayName("`memberId`로 멤버 삭제 - 정상적인 요청의 경우 멤버를 삭제한다.")
        void deleteMemberTest(){
            // given
            String memberId = RandomUUIDGenerator.generate();
            Mockito.when(mockMemberRepository.findByMemberId(memberId))
                    .thenReturn(Member.builder().memberId(memberId).build());

            //interactionRepository.deleteByInteractionMemberMemberId(memberId)

//            Mockito.doNothing()
//                    .when(mockInteractionRepository).deleteByInteractionMemberMemberId(Mockito.anyString());


            // when
            ServiceResult serviceResult = memberService.deleteMemberByMemberId(memberId);
            log.info("{}",serviceResult.getMessage());

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(1)).deleteByMemberId(Mockito.anyString());
            Mockito.verify(mockFeedRepository, Mockito.times(1)).findAllByOwnerMemberId(Mockito.anyString());
            Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());

        }

        @Test
        @DisplayName("`memberId`로 멤버 삭제 - 요청하는 `memberId`가 존재하지 않는 멤버인 경우 서비스 결과가 실패를 리턴해야 한다.")
        void deleteMemberTestWithNotExistsMemberId(){
            // given
            Mockito.doNothing()
                    .when(mockMemberRepository).deleteByMemberId(Mockito.anyString());
            Mockito.when(mockMemberRepository.findByMemberId(Mockito.anyString()))
                    .thenReturn(null);

            String memberId = RandomUUIDGenerator.generate();

            // when
            ServiceResult serviceResult = memberService.deleteMemberByMemberId(memberId);
            log.info("{}",serviceResult.getMessage());

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(0)).deleteByMemberId(Mockito.anyString());
            Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
        }

        @Test
        @DisplayName("`memberId`로 멤버 삭제 - 요청에 `memberId`가 누락된 경우 서비스 결과가 실패를 리턴해야 한다.")
        void deleteMemberTestWithNullMemberId(){
            // given
            Mockito.doNothing()
                    .when(mockMemberRepository).deleteByMemberId(Mockito.anyString());

            String nullMemberId = null;

            // when
            ServiceResult serviceResult = memberService.deleteMemberByMemberId(nullMemberId);
            log.info("{}",serviceResult.getMessage());

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(0)).deleteByMemberId(Mockito.anyString());
            Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
        }

        @Test
        @DisplayName("`memberId`로 멤버 삭제 " +
                "- 요청하는 `memberId`의 형태가 유효하지 않은 경우 서비스 결과가 실패를 리턴해야 한다.")
        void deleteMemberTestWithInvalidMemberIdFormat(){
            // given
            Mockito.doNothing()
                    .when(mockMemberRepository).deleteByMemberId(Mockito.anyString());

            String invalidMemberIdFormat = "invalidMemberIdFormat";

            // when
            ServiceResult serviceResult = memberService.deleteMemberByMemberId(invalidMemberIdFormat);
            log.info("{}",serviceResult.getMessage());

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(0)).deleteByMemberId(Mockito.anyString());
            Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
        }





    }

}
