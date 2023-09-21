package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Dto.RequestBody.InteractionCreationRequestBody;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Feed.FeedRepository;
import com.teamseven.MusicVillain.Interaction.Interaction;
import com.teamseven.MusicVillain.Interaction.InteractionRepository;
import com.teamseven.MusicVillain.Interaction.InteractionService;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Notification.NotificationRepository;
import com.teamseven.MusicVillain.Notification.Notification;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
@DisplayName("InteractionService 단위 테스트")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Slf4j
public class InteractionServiceUnitTest {
    @Mock
    InteractionRepository mockInteractionRepository;

    @Mock
    MemberRepository mockMemberRepository;

    @Mock
    FeedRepository mockFeedRepository;

    @Mock
    NotificationRepository mockNotificationRepository;

    @InjectMocks
    InteractionService mockInteractionService;

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Create 테스트")
    class CreateTest{
        @Nested
        @DisplayName("insertInteraction")
        class InsertInteraction{
            @Test
            @DisplayName("정상적인 요청이고, 해당 멤버가 해당 피드에 좋아요를 한 적 없다면, 좋아요로 동작")
            void insertInteractionTestWithNotExistsInteraction(){
                // given
                InteractionCreationRequestBody mockInteractionCreationRequestBody = InteractionCreationRequestBody.builder()
                        .feedId(RandomUUIDGenerator.generate())
                        .memberId(RandomUUIDGenerator.generate())
                        .build();
                Member mockMember = Member.builder().memberId(mockInteractionCreationRequestBody.getMemberId()).userId(RandomUUIDGenerator.generate()).userInfo("mockUserInfo").name("mockName").email("mockEmail").role("mockRole").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).lastLoginAt(LocalDateTime.now()).providerType("mockProviderType").build();
                Feed mockFeed = Feed.builder().feedId(mockInteractionCreationRequestBody.getFeedId()).feedType("mockFeedType").owner(mockMember).record(null).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).description("mockDescription").viewCount(0).musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockInteractionRepository.findByInteractionMemberAndInteractionFeed(mockMember, mockFeed)).thenReturn(null);
                Mockito.when(mockMemberRepository.findByMemberId(mockInteractionCreationRequestBody.getMemberId())).thenReturn(mockMember);
                Mockito.when(mockFeedRepository.findByFeedId(mockInteractionCreationRequestBody.getFeedId())).thenReturn(mockFeed);

                String mockInteractionId = UUID.randomUUID().toString().replace("-", "");
                Interaction mockInteraction = Interaction.builder()
                        .interactionId(mockInteractionId)
                        .interactionFeed(mockFeed)
                        .interactionMember(mockMember)
                        .build();
                Mockito.when(mockInteractionRepository.save(mockInteraction)).thenReturn(mockInteraction);
                Notification mockNotification = Notification.builder()
                        .notificationId(RandomUUIDGenerator.generate())
                        .interaction(mockInteraction)
                        .owner(mockFeed.getOwner())
                        .ownerRead(Notification.NOTIFICATION_UNREAD)
                        .createdAt(LocalDateTime.now())
                        .build();
                Mockito.when(mockNotificationRepository.save(mockNotification)).thenReturn(mockNotification);

                // when
                ServiceResult serviceResult = mockInteractionService.insertInteraction(mockInteractionCreationRequestBody);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).findByInteractionMemberAndInteractionFeed(mockMember, mockFeed);

                ArgumentCaptor<Interaction> interactionCaptor = ArgumentCaptor.forClass(Interaction.class);
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).save(interactionCaptor.capture());
                Interaction capturedInteraction = interactionCaptor.getValue();
                Assertions.assertEquals(mockFeed, capturedInteraction.getInteractionFeed());
                Assertions.assertEquals(mockMember, capturedInteraction.getInteractionMember());

                ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
                Mockito.verify(mockNotificationRepository, Mockito.times(1)).save(notificationCaptor.capture());
                Notification capturedNotification = notificationCaptor.getValue();
                Assertions.assertEquals(mockFeed.getOwner(), capturedNotification.getOwner());
                Assertions.assertEquals(Notification.NOTIFICATION_UNREAD, capturedNotification.getOwnerRead());

                Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
                Assertions.assertEquals("Interaction created", serviceResult.getMessage());
                Assertions.assertNotNull(serviceResult.getData());
            }

            @Test
            @DisplayName("정상적인 요청이고, 해당 멤버가 해당 피드에 좋아요를 한 적 있다면, 좋아요 취소로 동작")
            void insertInteractionTestWithExistsInteraction(){
                // given
                InteractionCreationRequestBody mockInteractionCreationRequestBody = InteractionCreationRequestBody.builder()
                        .feedId(RandomUUIDGenerator.generate())
                        .memberId(RandomUUIDGenerator.generate())
                        .build();
                Member mockMember = Member.builder().memberId(mockInteractionCreationRequestBody.getMemberId()).userId(RandomUUIDGenerator.generate()).userInfo("mockUserInfo").name("mockName").email("mockEmail").role("mockRole").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).lastLoginAt(LocalDateTime.now()).providerType("mockProviderType").build();
                Feed mockFeed = Feed.builder().feedId(mockInteractionCreationRequestBody.getFeedId()).feedType("mockFeedType").owner(mockMember).record(null).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).description("mockDescription").viewCount(0).musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockInteractionRepository.findByInteractionMemberAndInteractionFeed(mockMember, mockFeed)).thenReturn(Interaction.builder().interactionId(RandomUUIDGenerator.generate()).interactionFeed(mockFeed).interactionMember(mockMember).build());
                Mockito.when(mockMemberRepository.findByMemberId(mockInteractionCreationRequestBody.getMemberId())).thenReturn(mockMember);
                Mockito.when(mockFeedRepository.findByFeedId(mockInteractionCreationRequestBody.getFeedId())).thenReturn(mockFeed);

                // when
                ServiceResult serviceResult = mockInteractionService.insertInteraction(mockInteractionCreationRequestBody);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).findByInteractionMemberAndInteractionFeed(mockMember, mockFeed);

                ArgumentCaptor<Interaction> interactionCaptor = ArgumentCaptor.forClass(Interaction.class);
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).delete(interactionCaptor.capture());
                Interaction capturedInteraction = interactionCaptor.getValue();
                Assertions.assertEquals(mockFeed, capturedInteraction.getInteractionFeed());
                Assertions.assertEquals(mockMember, capturedInteraction.getInteractionMember());

                Mockito.verify(mockNotificationRepository, Mockito.times(1)).deleteByInteraction(capturedInteraction);

                Assertions.assertEquals(ServiceResult.SUCCESS, serviceResult.getResult());
                // Assertions.assertEquals("Interaction deleted", serviceResult.getMessage());
                // Assertions.assertNull(serviceResult.getData());
            }

            @Test
            @DisplayName("해당하는 멤버나 피드가 존재하지 않을 경우, 인터랙션을 생성할 수 없다.")
            void insertInteractionTestWithNotExistsMemberOrFeed(){
                // given
                InteractionCreationRequestBody mockInteractionCreationRequestBody = InteractionCreationRequestBody.builder()
                        .feedId(RandomUUIDGenerator.generate())
                        .memberId(RandomUUIDGenerator.generate())
                        .build();
                Member mockMember = Member.builder().memberId(mockInteractionCreationRequestBody.getMemberId()).userId(RandomUUIDGenerator.generate()).userInfo("mockUserInfo").name("mockName").email("mockEmail").role("mockRole").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).lastLoginAt(LocalDateTime.now()).providerType("mockProviderType").build();
                Feed mockFeed = Feed.builder().feedId(mockInteractionCreationRequestBody.getFeedId()).feedType("mockFeedType").owner(mockMember).record(null).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).description("mockDescription").viewCount(0).musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockInteractionRepository.findByInteractionMemberAndInteractionFeed(mockMember, mockFeed)).thenReturn(null);
                Mockito.when(mockMemberRepository.findByMemberId(mockInteractionCreationRequestBody.getMemberId())).thenReturn(null);
                Mockito.when(mockFeedRepository.findByFeedId(mockInteractionCreationRequestBody.getFeedId())).thenReturn(null);

                // when
                ServiceResult serviceResult = mockInteractionService.insertInteraction(mockInteractionCreationRequestBody);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).findByInteractionMemberAndInteractionFeed(mockMember, mockFeed);
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).save(Mockito.any(Interaction.class));
                Mockito.verify(mockNotificationRepository, Mockito.times(0)).save(Mockito.any(Notification.class));

                Assertions.assertEquals(ServiceResult.FAIL, serviceResult.getResult());
                Assertions.assertEquals("Member or Feed not found", serviceResult.getMessage());
                Assertions.assertNull(serviceResult.getData());
            }
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Read 테스트")
    class ReadTest{

        int mockInteractionListSize;
        @BeforeEach
        void setUp(){
            Mockito.reset(mockInteractionRepository);

            String interactionId1 = RandomUUIDGenerator.generate();
            String interactionId2 = RandomUUIDGenerator.generate();
            String interactionId3 = RandomUUIDGenerator.generate();

            List<Interaction> mockInteractionList = List.of(
                    Interaction.builder().interactionId(interactionId1).interactionFeed(Feed.builder().feedId(RandomUUIDGenerator.generate()).build()).interactionMember(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).build(),
                    Interaction.builder().interactionId(interactionId2).interactionFeed(Feed.builder().feedId(RandomUUIDGenerator.generate()).build()).interactionMember(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).build(),
                    Interaction.builder().interactionId(interactionId3).interactionFeed(Feed.builder().feedId(RandomUUIDGenerator.generate()).build()).interactionMember(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).build()
            );
            mockInteractionListSize = mockInteractionList.size();

            Mockito.when(mockInteractionRepository.findAll()).thenReturn(mockInteractionList);
        }
        @Nested
        @DisplayName("getAllInteractions")
        class GetAllInteractions{
            @Test
            @DisplayName("존재하는 모든 인터렉션을 조회할 수 있다.")
            void getAllInteractionsTest(){
                // given

                // when
                List dataTransferObjectList = mockInteractionService.getAllInteractions();

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).findAll();

                Assertions.assertEquals(mockInteractionListSize, dataTransferObjectList.size());
            }
        }

        @Nested
        @DisplayName("getInteractionCountByFeedId")
        class GetInteractionCountByFeedId{
            @Test
            @DisplayName("유효한 피드 식별자로 특정 피드의 인터렉션 개수를 조회할 수 있다.")
            void getInteractionCountByFeedIdTest(){
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                int mockInteractionCount = mockInteractionRepository.countByInteractionFeedFeedId(mockFeedId);

                Mockito.when(mockInteractionRepository.countByInteractionFeedFeedId(mockFeedId)).thenReturn(mockInteractionCount);
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(new Feed());  // Feed 객체를 실제 정의에 맞게 생성해 주세요.

                // when
                ServiceResult serviceResult = mockInteractionService.getInteractionCountByFeedId(mockFeedId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(2)).countByInteractionFeedFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.success(mockInteractionCount), serviceResult);
            }


            @Test
            @DisplayName("존재하지 않는 피드 식별자의 경우 조회할 수 없다.")
            void getInteractionCountByFeedIdTestWithNotExistsFeedId(){
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                int mockInteractionCount = mockInteractionRepository.countByInteractionFeedFeedId(mockFeedId);
                Mockito.when(mockInteractionRepository.countByInteractionFeedFeedId(mockFeedId)).thenReturn(mockInteractionCount);

                // when
                ServiceResult serviceResult = mockInteractionService.getInteractionCountByFeedId(mockFeedId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).countByInteractionFeedFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.fail("Feed not found"), serviceResult);
            }

            @Test
            @DisplayName("피드 식별자가 누락된 경우 조회할 수 없다.")
            void getInteractionCountByFeedIdTestWithFeedIdNull(){
                // given
                String mockFeedId = null;

                // when
                ServiceResult serviceResult = mockInteractionService.getInteractionCountByFeedId(mockFeedId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).countByInteractionFeedFeedId(mockFeedId);
                Assertions.assertEquals(ServiceResult.fail("Feed ID is null"), serviceResult);
            }
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Delete 테스트")
    class DeleteTest {
        @Nested
        @DisplayName("deleteInteractionByInteractionId")
        class DeleteInteractionByInteractionId {
            @Test
            @DisplayName("유효한 인터렉션 식별자로 인터렉션을 삭제할 수 있다.")
            void deleteInteractionByInteractionIdTest() {
                // given
                String mockInteractionId = RandomUUIDGenerator.generate();
                Interaction mockInteraction = Interaction.builder().interactionId(mockInteractionId).interactionFeed(Feed.builder().feedId(RandomUUIDGenerator.generate()).build()).interactionMember(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).build();
                Mockito.when(mockInteractionRepository.findByInteractionId(mockInteractionId)).thenReturn(mockInteraction);
                Feed mockFeed = mockInteraction.getInteractionFeed();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeed.getFeedId())).thenReturn(mockFeed);
                int mockInteractionCount = mockInteractionRepository.countByInteractionFeedFeedId(mockFeed.getFeedId());

                // when
                mockInteractionService.deleteInteractionByInteractionId(mockInteractionId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).findByInteractionId(mockInteractionId);
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).deleteByInteractionId(mockInteractionId);
            }

            @Test
            @DisplayName("존재하지 않는 인터렉션 식별자로 인터렉션을 삭제할 수 없다.")
            void deleteInteractionByInteractionIdTestWithNotExistsInteractionId() {
                // given
                String mockInteractionId = RandomUUIDGenerator.generate();
                Interaction mockInteraction = Interaction.builder().interactionId(mockInteractionId).interactionFeed(Feed.builder().feedId(RandomUUIDGenerator.generate()).build()).interactionMember(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).build();
                Mockito.when(mockInteractionRepository.findByInteractionId(mockInteractionId)).thenReturn(null);

                // when
                mockInteractionService.deleteInteractionByInteractionId(mockInteractionId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).findByInteractionId(mockInteractionId);
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).deleteByInteractionId(mockInteractionId);
            }

            @Test
            @DisplayName("인터렉션 식별자가 누락된 경우 인터렉션을 삭제할 수 없다.")
            void deleteInteractionByInteractionIdTestWithInteractionIdNull() {
                // given
                String mockInteractionId = null;

                // when
                mockInteractionService.deleteInteractionByInteractionId(mockInteractionId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).findByInteractionId(mockInteractionId);
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).deleteByInteractionId(mockInteractionId);
            }
        }

        @Nested
        @DisplayName("deleteInteractionByMemberId")
        class DeleteInteractionByMemberId {
            @Test
            @DisplayName("유효한 회원 식별자로 인터렉션을 삭제할 수 있다.")
            void deleteInteractionByMemberIdTest() {
                // given
                String mockMemberId = RandomUUIDGenerator.generate();
                Member mockMember = Member.builder().memberId(mockMemberId).userId(RandomUUIDGenerator.generate()).userInfo("mockUserInfo").name("mockName").email("mockEmail").role("mockRole").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).lastLoginAt(LocalDateTime.now()).providerType("mockProviderType").build();
                Mockito.when(mockMemberRepository.findByMemberId(mockMemberId)).thenReturn(mockMember);
                List<Interaction> mockInteractionList = List.of(
                        Interaction.builder().interactionId(RandomUUIDGenerator.generate()).interactionFeed(Feed.builder().feedId(RandomUUIDGenerator.generate()).build()).interactionMember(mockMember).build(),
                        Interaction.builder().interactionId(RandomUUIDGenerator.generate()).interactionFeed(Feed.builder().feedId(RandomUUIDGenerator.generate()).build()).interactionMember(mockMember).build(),
                        Interaction.builder().interactionId(RandomUUIDGenerator.generate()).interactionFeed(Feed.builder().feedId(RandomUUIDGenerator.generate()).build()).interactionMember(mockMember).build()
                );
                Mockito.when(mockInteractionRepository.findAll()).thenReturn(mockInteractionList);
                List<String> mockInteractionIdListToDelete = mockInteractionList.stream()
                        .filter(interaction -> interaction.getInteractionMember().getMemberId().equals(mockMemberId))
                        .map(Interaction::getInteractionId)
                        .collect(Collectors.toList());

                // when
                mockInteractionService.deleteInteractionByMemberId(mockMemberId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).findAll();
            }

            @Test
            @DisplayName("존재하지 않는 회원 식별자로 인터렉션을 삭제할 수 없다.")
            void deleteInteractionByMemberIdTestWithNotExistsMemberId() {
                // given
                String mockMemberId = RandomUUIDGenerator.generate();
                Member mockMember = Member.builder().memberId(mockMemberId).userId(RandomUUIDGenerator.generate()).userInfo("mockUserInfo").name("mockName").email("mockEmail").role("mockRole").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).lastLoginAt(LocalDateTime.now()).providerType("mockProviderType").build();
                Mockito.when(mockMemberRepository.findByMemberId(mockMemberId)).thenReturn(null);

                // when
                mockInteractionService.deleteInteractionByMemberId(mockMemberId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).findAll();
            }

            @Test
            @DisplayName("인터렉션이 없는 경우 인터렉션을 삭제할 수 없다.")
            void deleteInteractionByMemberIdTestWithNotExistsInteraction() {
                // given
                String mockMemberId = RandomUUIDGenerator.generate();
                Member mockMember = Member.builder().memberId(mockMemberId).userId(RandomUUIDGenerator.generate()).userInfo("mockUserInfo").name("mockName").email("mockEmail").role("mockRole").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).lastLoginAt(LocalDateTime.now()).providerType("mockProviderType").build();
                Mockito.when(mockMemberRepository.findByMemberId(mockMemberId)).thenReturn(mockMember);
                List<Interaction> mockInteractionList = List.of();
                Mockito.when(mockInteractionRepository.findAll()).thenReturn(mockInteractionList);

                // when
                mockInteractionService.deleteInteractionByMemberId(mockMemberId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).findAll();
            }

            @Test
            @DisplayName("회원 식별자가 누락된 경우 인터렉션을 삭제할 수 없다.")
            void deleteInteractionByMemberIdTestWithMemberIdNull() {
                // given
                String mockMemberId = null;

                // when
                mockInteractionService.deleteInteractionByMemberId(mockMemberId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).findAll();
            }
        }

        @Nested
        @DisplayName("deleteInteractionByFeedId")
        class DeleteInteractionByFeedId {
            @Test
            @DisplayName("유효한 피드 식별자로 인터렉션을 삭제할 수 있다.")
            void deleteInteractionByFeedIdTest() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").owner(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).record(null).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).description("mockDescription").viewCount(0).musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(mockFeed);
                List<Interaction> mockInteractionList = List.of(
                        Interaction.builder().interactionId(RandomUUIDGenerator.generate()).interactionFeed(mockFeed).interactionMember(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).build(),
                        Interaction.builder().interactionId(RandomUUIDGenerator.generate()).interactionFeed(mockFeed).interactionMember(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).build(),
                        Interaction.builder().interactionId(RandomUUIDGenerator.generate()).interactionFeed(mockFeed).interactionMember(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).build()
                );
                Mockito.when(mockInteractionRepository.findByInteractionFeedFeedId(mockFeedId)).thenReturn(mockInteractionList);

                // when
                mockInteractionService.deleteInterationsByFeedId(mockFeedId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).findByInteractionFeedFeedId(mockFeedId);
                Mockito.verify(mockNotificationRepository, Mockito.times(1)).deleteByInteraction(mockInteractionList.get(0));
                Mockito.verify(mockNotificationRepository, Mockito.times(1)).deleteByInteraction(mockInteractionList.get(1));
                Mockito.verify(mockNotificationRepository, Mockito.times(1)).deleteByInteraction(mockInteractionList.get(2));
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).deleteByInteractionFeedFeedId(mockFeedId);
            }

            @Test
            @DisplayName("존재하지 않는 피드 식별자로 인터렉션을 삭제할 수 없다.")
            void deleteInteractionByFeedIdTestWithNotExistsFeedId() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").owner(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).record(null).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).description("mockDescription").viewCount(0).musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(null);

                // when
                mockInteractionService.deleteInterationsByFeedId(mockFeedId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).findByInteractionFeedFeedId(mockFeedId);
                Mockito.verify(mockNotificationRepository, Mockito.times(0)).deleteByInteraction(Mockito.any(Interaction.class));
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).deleteByInteractionFeedFeedId(mockFeedId);
            }

            @Test
            @DisplayName("인터렉션이 없는 경우 인터렉션을 삭제할 수 없다.")
            void deleteInteractionByFeedIdTestWithNotExistsInteraction() {
                // given
                String mockFeedId = RandomUUIDGenerator.generate();
                Feed mockFeed = Feed.builder().feedId(mockFeedId).feedType("mockFeedType").owner(Member.builder().memberId(RandomUUIDGenerator.generate()).build()).record(null).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).description("mockDescription").viewCount(0).musicName("mockMusicName").musicianName("mockMusicianName").build();
                Mockito.when(mockFeedRepository.findByFeedId(mockFeedId)).thenReturn(mockFeed);
                List<Interaction> mockInteractionList = List.of();
                Mockito.when(mockInteractionRepository.findByInteractionFeedFeedId(mockFeedId)).thenReturn(mockInteractionList);

                // when
                mockInteractionService.deleteInterationsByFeedId(mockFeedId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(1)).findByInteractionFeedFeedId(mockFeedId);
                Mockito.verify(mockNotificationRepository, Mockito.times(0)).deleteByInteraction(Mockito.any(Interaction.class));
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).deleteByInteractionFeedFeedId(mockFeedId);
            }

            @Test
            @DisplayName("피드 식별자가 누락된 경우 인터렉션을 삭제할 수 없다.")
            void deleteInteractionByFeedIdTestWithFeedIdNull() {
                // given
                String mockFeedId = null;

                // when
                mockInteractionService.deleteInterationsByFeedId(mockFeedId);

                // then
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).findByInteractionFeedFeedId(mockFeedId);
                Mockito.verify(mockNotificationRepository, Mockito.times(0)).deleteByInteraction(Mockito.any(Interaction.class));
                Mockito.verify(mockInteractionRepository, Mockito.times(0)).deleteByInteractionFeedFeedId(mockFeedId);
            }
        }
    }
}
