package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Dto.Converter.DtoConverter;
import com.teamseven.MusicVillain.Dto.Converter.FeedDtoDtoConverter;
import com.teamseven.MusicVillain.Dto.DataTransferObject;
import com.teamseven.MusicVillain.Dto.FeedDto;
import com.teamseven.MusicVillain.Dto.ResponseBody.RecordResponseBody;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Record.RecordRepository;
import com.teamseven.MusicVillain.Security.JWT.JwtTokenRepository;
import com.teamseven.MusicVillain.Feed.FeedService;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@DisplayName("FeedService 단위 테스트")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Slf4j
public class FeedServiceUnitTest {
    @Mock
    FeedRepository mockFeedRepository;

    @Mock
    MemberRepository mockMemberRepository;

    @Mock
    RecordRepository mockRecordRepository;

    @Mock
    JwtTokenRepository mockJwtTokenRepository;

    @InjectMocks
    FeedService feedService;


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Create 테스트 (insertFeed)")
    class CreateTest{
        @Test
        @Order(1)
        @DisplayName("정상적인 요청의 경우 동일한 정보를 가진 피드가 생성되어야 한다.")
        void createFeedTest() throws IOException {
            // given
            String mockOwnerId = "mockOwnerId";
            String mockFeedType = "mockFeedType";
            String mockFeedDescription = "mockFeedDescription";
            int mockRecordDuration = 100;
            MultipartFile mockRecordFile = new MockMultipartFile("mockRecordFile", "mockRecordFile".getBytes());
            String mockMusicName = "mockMusicName";
            String mockMusicianName = "mockMusicianName";
            Mockito.when(mockMemberRepository.findByMemberId(mockOwnerId)).thenReturn(Member.builder().memberId(mockOwnerId).build());
            Mockito.when(mockRecordRepository.save(Mockito.any())).thenReturn(null);
            Mockito.when(mockFeedRepository.save(Mockito.any())).thenReturn(null);

            // when
            ServiceResult serviceResult = feedService.insertFeed(mockOwnerId, mockFeedType, mockFeedDescription, mockRecordDuration, mockRecordFile, mockMusicName, mockMusicianName);

            // then
            String createdFeed = (String) serviceResult.getData();

            Mockito.verify(mockMemberRepository, Mockito.times(1)).findByMemberId(mockOwnerId);
            Mockito.verify(mockRecordRepository, Mockito.times(1)).save(Mockito.any());
            Mockito.verify(mockFeedRepository, Mockito.times(1)).save(Mockito.any());

            Assertions.assertEquals(serviceResult.getResult(), ServiceResult.SUCCESS);

        }

        @Test
        @Order(2)
        @DisplayName("필요한 인자가 누락된 경우 생성되지 않아야 한다. - `ownerId` 누락")
        public void createFeedTestWithOwnerIdNull() throws IOException{
            // given
            String mockOwnerId = null;
            String mockFeedType = "mockFeedType";
            String mockFeedDescription = "mockFeedDescription";
            int mockRecordDuration = 100;
            MultipartFile mockRecordFile = new MockMultipartFile("mockRecordFile", "mockRecordFile".getBytes());
            String mockMusicName = "mockMusicName";
            String mockMusicianName = "mockMusicianName";
            Mockito.when(mockMemberRepository.findByMemberId(mockOwnerId)).thenReturn(null);

            // when
            ServiceResult serviceResult = feedService.insertFeed(mockOwnerId, mockFeedType, mockFeedDescription, mockRecordDuration, mockRecordFile, mockMusicName, mockMusicianName);

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(1)).findByMemberId(mockOwnerId);
            Mockito.verify(mockRecordRepository, Mockito.times(0)).save(Mockito.any());
            Mockito.verify(mockFeedRepository, Mockito.times(0)).save(Mockito.any());

            Assertions.assertEquals(serviceResult.getResult(), ServiceResult.FAIL);

        }

        @Test
        @Order(3)
        @DisplayName("필요한 인자가 누락된 경우 생성되지 않아야 한다. - `musicName` 누락")
        public void createFeedTestWithMusicNameNull() throws IOException{
            // given
            String mockOwnerId = "mockOwnerId";
            String mockFeedType = "mockFeedType";
            String mockFeedDescription = "mockFeedDescription";
            int mockRecordDuration = 100;
            MultipartFile mockRecordFile = new MockMultipartFile("mockRecordFile", "mockRecordFile".getBytes());
            String mockMusicName = null;
            String mockMusicianName = "mockMusicianName";
            Mockito.when(mockMemberRepository.findByMemberId(mockOwnerId)).thenReturn(Member.builder().memberId(mockOwnerId).build());

            // when
            ServiceResult serviceResult = feedService.insertFeed(mockOwnerId, mockFeedType, mockFeedDescription, mockRecordDuration, mockRecordFile, mockMusicName, mockMusicianName);

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(1)).findByMemberId(mockOwnerId);
            Mockito.verify(mockRecordRepository, Mockito.times(0)).save(Mockito.any());
            Mockito.verify(mockFeedRepository, Mockito.times(0)).save(Mockito.any());

            Assertions.assertEquals(serviceResult.getResult(), ServiceResult.FAIL);

        }

        @Test
        @Order(2)
        @DisplayName("필요한 인자가 누락된 경우 생성되지 않아야 한다. - `feedType` 누락")
        public void createFeedTestWithFeedTypeNull() throws IOException{
            // given
            Mockito.reset(mockRecordRepository);
            Mockito.reset(mockFeedRepository);

            String mockOwnerId = "mockOwnerId";
            String mockFeedType = null;
            String mockFeedDescription = "mockFeedDescription";
            int mockRecordDuration = 100;
            MultipartFile mockRecordFile = new MockMultipartFile("mockRecordFile", "mockRecordFile".getBytes());
            String mockMusicName = "mockMusicName";
            String mockMusicianName = "mockMusicianName";
            Mockito.when(mockMemberRepository.findByMemberId(mockOwnerId)).thenReturn(Member.builder().memberId(mockOwnerId).build());

            // when
            ServiceResult serviceResult = feedService.insertFeed(mockOwnerId, mockFeedType, mockFeedDescription, mockRecordDuration, mockRecordFile, mockMusicName, mockMusicianName);

            // then
            Mockito.verify(mockMemberRepository, Mockito.times(1)).findByMemberId(mockOwnerId);
            Mockito.verify(mockRecordRepository, Mockito.times(0)).save(Mockito.any());
            Mockito.verify(mockFeedRepository, Mockito.times(0)).save(Mockito.any());

            Assertions.assertEquals(serviceResult.getResult(), ServiceResult.FAIL);

        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Read 테스트 (getFeedByFeedId)")
    class getFeedByFeedIdTest{
        int mockFeedListSize;
        @BeforeEach
        void setUp(){
            Mockito.reset(mockFeedRepository);

            String feedId1 = RandomUUIDGenerator.generate();
            String feedId2 = RandomUUIDGenerator.generate();
            String feedId3 = RandomUUIDGenerator.generate();

            List<Feed> mockFeedList = List.of(
                    Feed.builder().feedId(feedId1).feedType("mockFeedType1").description("mockFeedDescription1").musicName("mockMusicName1").musicianName("mockMusicianName1").build(),
                    Feed.builder().feedId(feedId2).feedType("mockFeedType2").description("mockFeedDescription2").musicName("mockMusicName2").musicianName("mockMusicianName2").build(),
                    Feed.builder().feedId(feedId3).feedType("mockFeedType3").description("mockFeedDescription3").musicName("mockMusicName3").musicianName("mockMusicianName3").build()
            );
            mockFeedListSize = mockFeedList.size();

            Mockito.when(mockFeedRepository.findAllByOrderByCreatedAtDesc()).thenReturn(mockFeedList);
        }

        @Test
        @Order(1)
        @DisplayName("존재하는 모든 피드를 내림차순으로 조회할 수 있다.")
        void getAllFeedsTest(){
            // given

            // when
            ServiceResult dataTransferObjectList = feedService.getAllFeeds();

            // then
            Mockito.verify(mockFeedRepository, Mockito.times(1)).findAllByOrderByCreatedAtDesc();

            Assertions.assertEquals(mockFeedListSize, ((List<DataTransferObject>)dataTransferObjectList.getData()).size());
        }

        @Test
        @DisplayName("`feedId`로 조회 - 유효한 피드 식별자로 특정 피드의 정보를 조회할 수 있다.")
        void getFeedByFeedIdTest(){
            // given
            String mockFeedId = RandomUUIDGenerator.generate();
            Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").description("mockFeedDescription").musicName("mockMusicName").musicianName("mockMusicianName").build();
            Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(mockFeed);

            // when
            ServiceResult serviceResult = feedService.getFeedByFeedId(mockFeedId);

            // then
            FeedDto feedDto = (FeedDto)serviceResult.getData();

            Mockito.verify(mockFeedRepository, Mockito.times(1)).findByFeedId(mockFeedId);
            Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
            Assertions.assertEquals(mockFeedId, feedDto.getFeedId());
        }

        @Test
        @DisplayName("`feedId`로 조회 - 존재하지 않는 피드 식별자의 경우 조회할 수 없다.")
        void getFeedByFeedIdTestWithNotExistsFeedId(){
            // given
            String mockFeedId = RandomUUIDGenerator.generate();
            Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").description("mockFeedDescription").musicName("mockMusicName").musicianName("mockMusicianName").build();
            Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(null);

            // when
            ServiceResult serviceResult = feedService.getFeedByFeedId(mockFeedId);

            // then
            FeedDto feedDto = (FeedDto)serviceResult.getData();

            Mockito.verify(mockFeedRepository, Mockito.times(1)).findByFeedId(mockFeedId);
            Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
        }

        @Test
        @DisplayName("`feedId`로 조회 - 피드 식별자가 누락된 경우 조회할 수 없다.")
        void getFeedByFeedIdTestWithFeedIdNull(){
            // given
            String mockFeedId = null;

            // when
            ServiceResult serviceResult = feedService.getFeedByFeedId(mockFeedId);

            // then
            FeedDto feedDto = (FeedDto)serviceResult.getData();

            Mockito.verify(mockFeedRepository, Mockito.times(0)).findByFeedId(mockFeedId);
            Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
        }
    }

}
