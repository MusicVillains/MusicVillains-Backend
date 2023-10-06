package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Dto.NotificationDto;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Interaction.Interaction;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Notification.Notification;
import com.teamseven.MusicVillain.Notification.NotificationRepository;
import com.teamseven.MusicVillain.Notification.NotificationService;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@DisplayName("NotificationService 단위 테스트")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Slf4j
public class NotificationServiceUnitTest {
    @Mock
    MemberRepository mockMemberRepository;

    @Mock
    NotificationRepository mockNotificationRepository;

    @InjectMocks
    NotificationService mockNotificationService;

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Read 테스트")
    class ReadTest {
        @Nested
        @DisplayName("getNotificationsByOwnerMemberID")
        class GetNotificationsByOwnerMemberID {

            String memberId;
            Member mockMember;
            int mockNotificationListSize;
            List<Notification> mockNotificationList;

            @BeforeEach
            void setUp() {
                memberId = "someMemberId";
                mockMember = Member.builder().memberId(memberId).build();

                mockNotificationList = List.of(
                        Notification.builder().notificationId("notificationId1").owner(mockMember).interaction(Interaction.builder().interactionId("interactionId1").build()).ownerRead(Notification.NOTIFICATION_UNREAD).createdAt(LocalDateTime.now()).build(),
                        Notification.builder().notificationId("notificationId2").owner(mockMember).interaction(Interaction.builder().interactionId("interactionId2").build()).ownerRead(Notification.NOTIFICATION_UNREAD).createdAt(LocalDateTime.now()).build()
                );

                mockNotificationListSize = mockNotificationList.size();

                Mockito.when(mockMemberRepository.findByMemberId(memberId)).thenReturn(mockMember);
                Mockito.when(mockNotificationRepository.findByOwnerMemberId(memberId)).thenReturn(mockNotificationList);
            }

            @Test
            @DisplayName("유효한 멤버 식별자로 특정 멤버의 모든 알림을 조회할 수 있다.")
            void getNotificationsByOwnerMemberIDTest() {
                // Execute
                var result = mockNotificationService.getNotificaitonsByOwnerMemberID(memberId);

                // Verify
                Assertions.assertEquals(ServiceResult.SUCCESS, result.getResult());
                Assertions.assertEquals(mockNotificationListSize, ((List<NotificationDto>) result.getData()).size());
            }

            @Test
            @DisplayName("유효하지 않은 멤버 식별자로 특정 멤버의 모든 알림을 조회할 수 없다.")
            void getNotificationsByOwnerMemberIDTestWithNotExistsMemberId(){
                // given
                String memberId = "someMemberId";
                Member mockMember = Member.builder().memberId(memberId).build();

                Mockito.when(mockMemberRepository.findByMemberId(memberId)).thenReturn(null);

                // when
                var result = mockNotificationService.getNotificaitonsByOwnerMemberID(memberId);

                // then
                Assertions.assertEquals(ServiceResult.FAIL, result.getResult());
                Assertions.assertEquals("Member not found", result.getMessage());
            }

            @Test
            @DisplayName("알림이 없는 멤버의 알림을 조회하면 서비스 성공을 반환하고 해당 멤버에 대한 알림이 없다는 메시지를 반환한다.")
            void getNotificationsByOwnerMemberIDTestWithNotExistsNotification(){
                // given
                String memberId = "someMemberId";
                Member mockMember = Member.builder().memberId(memberId).build();

                Mockito.when(mockMemberRepository.findByMemberId(memberId)).thenReturn(mockMember);
                Mockito.when(mockNotificationRepository.findByOwnerMemberId(memberId)).thenReturn(null);

                // when
                var result = mockNotificationService.getNotificaitonsByOwnerMemberID(memberId);

                // then
                Assertions.assertEquals(ServiceResult.SUCCESS, result.getResult());
                Assertions.assertEquals("There are no notifications", result.getMessage());
            }
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Update 테스트")
    class UpdateTest {
        @Nested
        @DisplayName("readNotification")
        class ReadNotification {
            @Test
            @DisplayName("유효한 알림 식별자로 알림을 읽음 상태로 변경할 수 있다.")
            void readNotificationTest(){
                // given
                String notificationId = RandomUUIDGenerator.generate();
                Notification mockNotification = Notification.builder().notificationId(notificationId).ownerRead(Notification.NOTIFICATION_UNREAD).build();

                Mockito.when(mockNotificationRepository.findByNotificationId(notificationId)).thenReturn(mockNotification);
                Mockito.when(mockNotificationRepository.save(Mockito.any(Notification.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                var result = mockNotificationService.readNotification(notificationId);
                System.out.println("Result: " + result);  // 로깅

                // then
                Mockito.verify(mockNotificationRepository, Mockito.times(1)).save(mockNotification);

                Assertions.assertNotNull(result, "Result should not be null");
                Assertions.assertEquals(ServiceResult.SUCCESS, result.getResult());
                Assertions.assertEquals(Notification.NOTIFICATION_READ, mockNotification.ownerRead);
                Assertions.assertEquals("Notification read successfully", result.getData());
            }

            @Test
            @DisplayName("유효하지 않은 알림 식별자로 알림을 읽음 상태로 변경할 수 없다.")
            void readNotificationTestWithNotExistsNotificationId(){
                // given
                String notificationId = RandomUUIDGenerator.generate();

                Mockito.when(mockNotificationRepository.findByNotificationId(notificationId)).thenReturn(null);

                // when
                var result = mockNotificationService.readNotification(notificationId);

                // then
                Assertions.assertEquals(ServiceResult.FAIL, result.getResult());
                Assertions.assertEquals("Notification not found", result.getMessage());
            }

            @Test
            @DisplayName("이미 읽음 상태인 알림에 대해 읽음처리를 시도할 경우 서비스 성공을 반환하고 별도로 처리하지 않는다.")
            void readNotificationTestWithAlreadyReadNotification(){
                // given
                String notificationId = RandomUUIDGenerator.generate();
                Notification mockNotification = Notification.builder().notificationId(notificationId).ownerRead(Notification.NOTIFICATION_READ).build();

                Mockito.when(mockNotificationRepository.findByNotificationId(notificationId)).thenReturn(mockNotification);

                // when
                var result = mockNotificationService.readNotification(notificationId);

                // then
                Assertions.assertEquals(ServiceResult.SUCCESS, result.getResult());
                Assertions.assertEquals("Notification already read", result.getMessage());
            }
        }
    }

}
