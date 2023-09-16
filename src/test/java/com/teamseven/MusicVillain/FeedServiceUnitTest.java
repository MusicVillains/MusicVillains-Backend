package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Dto.DataTransferObject;
import com.teamseven.MusicVillain.Dto.FeedDto;
import com.teamseven.MusicVillain.Dto.ResponseBody.RecordResponseBody;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Interaction.InteractionService;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Record.Record;
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

    @Mock
    InteractionService mockInteractionService;

    @InjectMocks
    FeedService mockFeedService;

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Create 테스트")
    class CreateTest{
        @Nested
        @DisplayName("insertFeed")
        class InsertFeed{
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
                ServiceResult serviceResult = mockFeedService.insertFeed(mockOwnerId, mockFeedType, mockFeedDescription, mockRecordDuration, mockRecordFile, mockMusicName, mockMusicianName);

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
                ServiceResult serviceResult = mockFeedService.insertFeed(mockOwnerId, mockFeedType, mockFeedDescription, mockRecordDuration, mockRecordFile, mockMusicName, mockMusicianName);

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
                ServiceResult serviceResult = mockFeedService.insertFeed(mockOwnerId, mockFeedType, mockFeedDescription, mockRecordDuration, mockRecordFile, mockMusicName, mockMusicianName);

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
                ServiceResult serviceResult = mockFeedService.insertFeed(mockOwnerId, mockFeedType, mockFeedDescription, mockRecordDuration, mockRecordFile, mockMusicName, mockMusicianName);

                // then
                Mockito.verify(mockMemberRepository, Mockito.times(1)).findByMemberId(mockOwnerId);
                Mockito.verify(mockRecordRepository, Mockito.times(0)).save(Mockito.any());
                Mockito.verify(mockFeedRepository, Mockito.times(0)).save(Mockito.any());

                Assertions.assertEquals(serviceResult.getResult(), ServiceResult.FAIL);

            }
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Read 테스트")
    class ReadTest{

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

        @Nested
        @DisplayName("getAllFeeds")
        class GetAllFeeds {
            @Test
            @Order(1)
            @DisplayName("존재하는 모든 피드를 내림차순으로 조회할 수 있다.")
            void getAllFeedsTest() {
                // given

                // when
                ServiceResult dataTransferObjectList = mockFeedService.getAllFeeds();

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(1)).findAllByOrderByCreatedAtDesc();

                Assertions.assertEquals(mockFeedListSize, ((List<DataTransferObject>) dataTransferObjectList.getData()).size());
            }
        }

        @Nested
        @DisplayName("getAllFeedsByMemberId")
        class GetAllFeedsByMemberId {
            @Test
            @DisplayName("유효한 멤버 식별자로 특정 멤버가 생성한 모든 피드를 조회할 수 있다.")
            void getAllFeedsByMemberIdTest() {
                // given
                String mockMemberId = RandomUUIDGenerator.generate();
                List<Feed> mockFeedList = List.of(
                        Feed.builder().feedId(RandomUUIDGenerator.generate()).feedType("mockFeedType1").description("mockFeedDescription1").musicName("mockMusicName1").musicianName("mockMusicianName1").build(),
                        Feed.builder().feedId(RandomUUIDGenerator.generate()).feedType("mockFeedType2").description("mockFeedDescription2").musicName("mockMusicName2").musicianName("mockMusicianName2").build(),
                        Feed.builder().feedId(RandomUUIDGenerator.generate()).feedType("mockFeedType3").description("mockFeedDescription3").musicName("mockMusicName3").musicianName("mockMusicianName3").build()
                );
                Mockito.when(mockFeedRepository.findAllByOwnerMemberId(mockMemberId)).thenReturn(mockFeedList);

                // when
                ServiceResult serviceResult = mockFeedService.getAllFeedsByMemberId(mockMemberId);

                // then
                List<FeedDto> feedDtoList = (List<FeedDto>) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(1)).findAllByOwnerMemberId(mockMemberId);
                Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
                Assertions.assertEquals(mockFeedList.size(), feedDtoList.size());
            }

            @Test
            @DisplayName("존재하지 않는 멤버 식별자의 경우 조회할 수 없다.")
            void getAllFeedsByMemberIdTestWithNotExistsMemberId() {
                // given
                String mockMemberId = RandomUUIDGenerator.generate();
                Mockito.when(mockFeedRepository.findAllByOwnerMemberId(mockMemberId)).thenReturn(null);

                // when
                ServiceResult serviceResult = mockFeedService.getAllFeedsByMemberId(mockMemberId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(1)).findAllByOwnerMemberId(mockMemberId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("Feeds not found for memberId: " + mockMemberId, serviceResult.getMessage());
            }

            @Test
            @DisplayName("멤버 식별자가 누락된 경우 조회할 수 없다.")
            void getAllFeedsByMemberIdTestWithMemberIdNull() {
                // given
                String mockMemberId = null;

                // when
                ServiceResult serviceResult = mockFeedService.getAllFeedsByMemberId(mockMemberId);

                // then
                List<FeedDto> feedDtoList = (List<FeedDto>) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(0)).findAllByOwnerMemberId(mockMemberId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("MemberId is null", serviceResult.getMessage());
            }
        }

        @Nested
        @DisplayName("getAllFeedsByFeedType")
        class GetAllFeedsByFeedType {
            @Test
            @DisplayName("유효한 피드 타입으로 특정 타입의 모든 피드를 조회할 수 있다.")
            void getAllFeedsByFeedTypeTest() {
                // given
                String mockFeedType = "mockFeedType";
                List<Feed> mockFeedList = List.of(
                        Feed.builder().feedId(RandomUUIDGenerator.generate()).feedType(mockFeedType).description("mockFeedDescription1").musicName("mockMusicName1").musicianName("mockMusicianName1").build(),
                        Feed.builder().feedId(RandomUUIDGenerator.generate()).feedType(mockFeedType).description("mockFeedDescription2").musicName("mockMusicName2").musicianName("mockMusicianName2").build(),
                        Feed.builder().feedId(RandomUUIDGenerator.generate()).feedType(mockFeedType).description("mockFeedDescription3").musicName("mockMusicName3").musicianName("mockMusicianName3").build()
                );
                Mockito.when(mockFeedRepository.findAllByFeedType(mockFeedType)).thenReturn(mockFeedList);

                // when
                ServiceResult serviceResult = mockFeedService.getAllFeedsByFeedType(mockFeedType);

                // then
                List<FeedDto> feedDtoList = (List<FeedDto>) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(1)).findAllByFeedType(mockFeedType);
                Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
                Assertions.assertEquals(mockFeedList.size(), feedDtoList.size());
            }

            @Test
            @DisplayName("존재하지 않는 피드 타입의 경우 조회할 수 없다.")
            void getAllFeedsByFeedTypeTestWithNotExistsFeedType() {
                // given
                String mockFeedType = "mockFeedType";
                Mockito.when(mockFeedRepository.findAllByFeedType(mockFeedType)).thenReturn(null);

                // when
                ServiceResult serviceResult = mockFeedService.getAllFeedsByFeedType(mockFeedType);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(1)).findAllByFeedType(mockFeedType);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("Feeds not found for feedType: " + mockFeedType, serviceResult.getMessage());
            }

            @Test
            @DisplayName("피드 타입이 누락된 경우 조회할 수 없다.")
            void getAllFeedsByFeedTypeTestWithFeedTypeNull() {
                // given
                String mockFeedType = null;

                // when
                ServiceResult serviceResult = mockFeedService.getAllFeedsByFeedType(mockFeedType);

                // then
                List<FeedDto> feedDtoList = (List<FeedDto>) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(0)).findAllByFeedType(mockFeedType);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("FeedType is null", serviceResult.getMessage());
            }
        }

        @Nested
        @DisplayName("getFeedByFeedId")
        class GetAllFeedsByFeedId {
            @Test
            @DisplayName("유효한 피드 식별자로 특정 피드의 정보를 조회할 수 있다.")
            void getFeedByFeedIdTest() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").description("mockFeedDescription").musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(mockFeed);

                // when
                ServiceResult serviceResult = mockFeedService.getFeedByFeedId(mockFeedId);

                // then
                FeedDto feedDto = (FeedDto) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(1)).findByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
                Assertions.assertEquals(mockFeedId, feedDto.getFeedId());
            }

            @Test
            @DisplayName("존재하지 않는 피드 식별자의 경우 조회할 수 없다.")
            void getFeedByFeedIdTestWithNotExistsFeedId() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").description("mockFeedDescription").musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(null);

                // when
                ServiceResult serviceResult = mockFeedService.getFeedByFeedId(mockFeedId);

                // then
                FeedDto feedDto = (FeedDto) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(1)).findByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
            }

            @Test
            @DisplayName("피드 식별자가 누락된 경우 조회할 수 없다.")
            void getFeedByFeedIdTestWithFeedIdNull() {
                // given
                String mockFeedId = null;

                // when
                ServiceResult serviceResult = mockFeedService.getFeedByFeedId(mockFeedId);

                // then
                FeedDto feedDto = (FeedDto) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(0)).findByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
            }
        }

        @Nested
        @DisplayName("getInteractionFeedsByMemberId")
        class GetInteractionFeedsByMemberId {
            @Test
            @DisplayName("유효한 멤버 식별자로 특정 멤버가 좋아요한 모든 피드를 조회할 수 있다.")
            void getInteractionFeedsByMemberIdTest() {
                // given
                String mockMemberId = RandomUUIDGenerator.generate();
                List<Feed> mockFeedList = List.of(
                        Feed.builder().feedId(RandomUUIDGenerator.generate()).feedType("mockFeedType1").description("mockFeedDescription1").musicName("mockMusicName1").musicianName("mockMusicianName1").build(),
                        Feed.builder().feedId(RandomUUIDGenerator.generate()).feedType("mockFeedType2").description("mockFeedDescription2").musicName("mockMusicName2").musicianName("mockMusicianName2").build(),
                        Feed.builder().feedId(RandomUUIDGenerator.generate()).feedType("mockFeedType3").description("mockFeedDescription3").musicName("mockMusicName3").musicianName("mockMusicianName3").build()
                );
                Mockito.when(mockFeedRepository.findAllByOwnerMemberId(mockMemberId)).thenReturn(mockFeedList);

                // when
                ServiceResult serviceResult = mockFeedService.getAllFeedsByMemberId(mockMemberId);

                // then
                List<FeedDto> feedDtoList = (List<FeedDto>) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(1)).findAllByOwnerMemberId(mockMemberId);
                Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
                Assertions.assertEquals(mockFeedList.size(), feedDtoList.size());
            }

            @Test
            @DisplayName("존재하지 않는 멤버 식별자의 경우 조회할 수 없다.")
            void getInteractionFeedsByMemberIdTestWithNotExistsMemberId() {
                // given
                String mockMemberId = RandomUUIDGenerator.generate();
                Mockito.when(mockFeedRepository.findAllByOwnerMemberId(mockMemberId)).thenReturn(null);

                // when
                ServiceResult serviceResult = mockFeedService.getAllFeedsByMemberId(mockMemberId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(1)).findAllByOwnerMemberId(mockMemberId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("Feeds not found for memberId: " + mockMemberId, serviceResult.getMessage());
            }

            @Test
            @DisplayName("멤버 식별자가 누락된 경우 조회할 수 없다.")
            void getInteractionFeedsByMemberIdTestWithMemberIdNull() {
                // given
                String mockMemberId = null;

                // when
                ServiceResult serviceResult = mockFeedService.getAllFeedsByMemberId(mockMemberId);

                // then
                List<FeedDto> feedDtoList = (List<FeedDto>) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(0)).findAllByOwnerMemberId(mockMemberId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("MemberId is null", serviceResult.getMessage());
            }
        }

        @Nested
        @DisplayName("getFeedOwnerMemberIdByFeedId")
        class GetFeedOwnerMemberIdByFeedId {
            @Test
            @DisplayName("유효한 피드 식별자로 특정 피드의 주인 멤버 식별자를 조회할 수 있다.")
            void getFeedOwnerMemberIdByFeedIdTest() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                String mockMemberId = RandomUUIDGenerator.generate();
                Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").description("mockFeedDescription").musicName("mockMusicName").musicianName("mockMusicianName").owner(Member.builder().memberId(mockMemberId).build()).build();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(mockFeed);

                // when
                ServiceResult serviceResult = mockFeedService.getFeedOwnerMemberIdByFeedId(mockFeedId);

                // then
                String ownerId = (String) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(1)).findByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
                Assertions.assertEquals(mockMemberId, ownerId);
            }

            @Test
            @DisplayName("존재하지 않는 피드 식별자의 경우 조회할 수 없다.")
            void getFeedOwnerMemberIdByFeedIdTestWithNotExistsFeedId() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").description("mockFeedDescription").musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(null);

                // when
                ServiceResult serviceResult = mockFeedService.getFeedOwnerMemberIdByFeedId(mockFeedId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(1)).findByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("Feed Not Found", serviceResult.getMessage());
            }

            @Test
            @DisplayName("피드 식별자가 누락된 경우 조회할 수 없다.")
            void getFeedOwnerMemberIdByFeedIdTestWithFeedIdNull() {
                // given
                String mockFeedId = null;

                // when
                ServiceResult serviceResult = mockFeedService.getFeedOwnerMemberIdByFeedId(mockFeedId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(0)).findByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("FeedId is null", serviceResult.getMessage());
            }
        }

        @Nested
        @DisplayName("getRecordByFeedId")
        class GetRecordByFeedId {
            @Test
            @DisplayName("유효한 피드 식별자로 특정 피드의 녹음 파일의 세부 정보를 조회할 수 있다.")
            void getRecordByFeedIdTest() {
                // given
                String mockFeedId = "mockFeedId";
                Record mockRecord = Record.builder().recordId("mockRecordId").recordFileType("mockRecordFileType").recordFileSize(100).recordDuration(100).recordRawData("mockRecordRawData".getBytes()).build();
                Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").owner(Member.builder().memberId("mockMemberId").build()).record(mockRecord).build();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(mockFeed);

                // when
                RecordResponseBody serviceResult = mockFeedService.getRecordByFeedId(mockFeedId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(2)).findByFeedId(mockFeedId);
                Assertions.assertEquals(Status.OK.getStatusCode(), serviceResult.getStatusCode());
                Assertions.assertEquals("Record found", serviceResult.getMessage());
                Assertions.assertEquals(mockRecord.getRecordId(), serviceResult.getRecordId());
                Assertions.assertEquals(mockRecord.getRecordFileSize(), serviceResult.getRecordFileSize());
                Assertions.assertEquals(mockRecord.getRecordDuration(), serviceResult.getRecordDuration());
                Assertions.assertEquals(mockRecord.getRecordFileType(), serviceResult.getRecordFileType());
                Assertions.assertEquals(mockRecord.getRecordRawData(), serviceResult.getRecordRawData());
            }

            @Test
            @DisplayName("존재하지 않는 피드 식별자의 경우 조회할 수 없다.")
            void getRecordByFeedIdTestWithNotExistsFeedId() {
                // given
                String mockFeedId = "mockFeedId";
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(null);

                // when
                RecordResponseBody serviceResult = mockFeedService.getRecordByFeedId(mockFeedId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(2)).findByFeedId(mockFeedId);
                Assertions.assertEquals(Status.NOT_FOUND.getStatusCode(), serviceResult.getStatusCode());
                Assertions.assertEquals("Feed not found", serviceResult.getMessage());
            }

            @Test
            @DisplayName("피드 식별자가 누락된 경우 조회할 수 없다.")
            void getRecordByFeedIdTestWithFeedIdNull() {
                // given
                String mockFeedId = null;

                // when
                RecordResponseBody serviceResult = mockFeedService.getRecordByFeedId(mockFeedId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(0)).findByFeedId(mockFeedId);
                Assertions.assertEquals(Status.BAD_REQUEST.getStatusCode(), serviceResult.getStatusCode());
                Assertions.assertEquals("FeedId is null", serviceResult.getMessage());
            }
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Update 테스트")
    class UpdateTest {
        @Nested
        @DisplayName("feedViewCountUp")
        class FeedViewCountUp{
            @Test
            @DisplayName("유효한 피드 식별자로 특정 피드의 조회수를 1 증가시킬 수 있다.")
            void feedViewCountUpTest() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").description("mockFeedDescription").musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(mockFeed);
                int mockViewCount = mockFeed.getViewCount();

                // when
                ServiceResult serviceResult = mockFeedService.feedViewCountUp(mockFeedId);

                // then
                int viewCount = (int) serviceResult.getData();

                Mockito.verify(mockFeedRepository, Mockito.times(1)).findByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
                Assertions.assertEquals(mockViewCount+1, viewCount);
            }

            @Test
            @DisplayName("존재하지 않는 피드 식별자의 경우 조회수를 증가시킬 수 없다.")
            void feedViewCountUpTestWithNotExistsFeedId() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(null);

                // when
                ServiceResult serviceResult = mockFeedService.feedViewCountUp(mockFeedId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(1)).findByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("Feed Not Found", serviceResult.getMessage());
            }

            @Test
            @DisplayName("피드 식별자가 누락된 경우 조회수를 증가시킬 수 없다.")
            void feedViewCountUpTestWithFeedIdNull() {
                // given
                String mockFeedId = null;

                // when
                ServiceResult serviceResult = mockFeedService.feedViewCountUp(mockFeedId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(0)).findByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("FeedId is null", serviceResult.getMessage());
            }

        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Delete 테스트")
    class DeleteTest {
        @Nested
        @DisplayName("deleteFeedByFeedId")
        class DeleteFeedByFeedId {
            @Test
            @DisplayName("유효한 피드 식별자로 특정 피드를 삭제할 수 있다.")
            void deleteFeedByFeedIdTest() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").description("mockFeedDescription").musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(mockFeed);

                // when
                ServiceResult serviceResult = mockFeedService.deleteFeedByFeedId(mockFeedId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(1)).findByFeedId(mockFeedId);
                Mockito.verify(mockInteractionService, Mockito.times(1)).deleteInterationsByFeedId(mockFeedId);
                Mockito.verify(mockFeedRepository, Mockito.times(1)).deleteByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
            }

            @Test
            @DisplayName("존재하지 않는 피드 식별자의 경우 삭제할 수 없다.")
            void deleteFeedByFeedIdTestWithNotExistsFeedId() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(null);

                // when
                ServiceResult serviceResult = mockFeedService.deleteFeedByFeedId(mockFeedId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(1)).findByFeedId(mockFeedId);
                Mockito.verify(mockInteractionService, Mockito.times(0)).deleteInterationsByFeedId(mockFeedId);
                Mockito.verify(mockFeedRepository, Mockito.times(0)).deleteByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("Feed Not Found", serviceResult.getMessage());
            }

            @Test
            @DisplayName("피드 식별자가 누락된 경우 삭제할 수 없다.")
            void deleteFeedByFeedIdTestWithFeedIdNull() {
                // given
                String mockFeedId = null;

                // when
                ServiceResult serviceResult = mockFeedService.deleteFeedByFeedId(mockFeedId);

                // then
                Mockito.verify(mockFeedRepository, Mockito.times(0)).findByFeedId(mockFeedId);
                Mockito.verify(mockInteractionService, Mockito.times(0)).deleteInterationsByFeedId(mockFeedId);
                Mockito.verify(mockFeedRepository, Mockito.times(0)).deleteByFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("Bad request, feedId is null", serviceResult.getMessage());
            }


        }
    }
}
