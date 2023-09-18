package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Record.Record;
import com.teamseven.MusicVillain.Record.RecordRepository;
import com.teamseven.MusicVillain.Record.RecordService;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;

@SpringBootTest
@DisplayName("RecordService 단위 테스트")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Slf4j
public class RecordServiceUnitTest {
    @Mock
    RecordRepository mockRecordRepository;

    @InjectMocks
    RecordService mockRecordService;

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Read 테스트")
    class ReadTest {

        int mockRecordListSize;

        @BeforeEach
        void setUp() {
            Mockito.reset(mockRecordRepository);

            String recordId1 = RandomUUIDGenerator.generate();
            String recordId2 = RandomUUIDGenerator.generate();
            String recordId3 = RandomUUIDGenerator.generate();

            List<Record> mockRecordList = List.of(
                    Record.builder().recordId(recordId1).recordFileType("mockFileType").recordFileSize(100).recordDuration(100).recordRawData(new byte[100]).build(),
                    Record.builder().recordId(recordId2).recordFileType("mockFileType").recordFileSize(100).recordDuration(100).recordRawData(new byte[100]).build(),
                    Record.builder().recordId(recordId3).recordFileType("mockFileType").recordFileSize(100).recordDuration(100).recordRawData(new byte[100]).build()
            );
            mockRecordListSize = mockRecordList.size();

            Mockito.when(mockRecordRepository.findAll()).thenReturn(mockRecordList);
        }

        @Nested
        @DisplayName("getAllRecords")
        class GetAllRecords {
            @Test
            @DisplayName("존재하는 모든 Record를 반환한다.")
            void getAllRecordsTest() {
                // given

                // when
                List dataTransferObjectList = mockRecordService.getAllRecords();

                // then
                Mockito.verify(mockRecordRepository, Mockito.times(1)).findAll();

                Assertions.assertEquals(mockRecordListSize, dataTransferObjectList.size());
            }
        }

    }
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    //@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Delete 테스트")
    class DeleteTest {
        @Nested
        @DisplayName("deleteRecordByRecordId")
        class DeleteRecordByRecordId {
            @Test
            @DisplayName("유효한 레코드 식별자로 레코드를 삭제할 수 있다.")
            void deleteRecordByRecordIdTest() {
                // given
                String recordId = RandomUUIDGenerator.generate();
                Mockito.when(mockRecordRepository.findByRecordId(recordId)).thenReturn(new Record()); // Mock 설정

                // when
                mockRecordService.DeleteRecordByRecordId(recordId);

                // then
                Mockito.verify(mockRecordRepository, Mockito.times(1)).findByRecordId(recordId);
                Mockito.verify(mockRecordRepository, Mockito.times(1)).deleteByRecordId(recordId);
            }

            @Test
            @DisplayName("존재하지 않는 레코드 식별자의 경우 조회할 수 없다.")
            void deleteRecordByRecordIdTestWithNotExistsRecordId() {
                // given
                String recordId = RandomUUIDGenerator.generate();
                Mockito.when(mockRecordRepository.findByRecordId(recordId)).thenReturn(null);

                // when
                Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    mockRecordService.DeleteRecordByRecordId(recordId);
                });

                // then
                Mockito.verify(mockRecordRepository, Mockito.times(1)).findByRecordId(recordId);
                Mockito.verify(mockRecordRepository, Mockito.times(0)).deleteByRecordId(recordId);
            }

        }
    }
}
