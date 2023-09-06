package com.teamseven.MusicVillain.Withdrawal;

import com.teamseven.MusicVillain.Dto.Converter.DtoConverter;
import com.teamseven.MusicVillain.Dto.Converter.DtoConverterFactory;
import com.teamseven.MusicVillain.Dto.DataTransferObject;
import com.teamseven.MusicVillain.Dto.RequestBody.WithdrawalCreationRequestBody;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Dto.WithdrawalDto;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;

    private DtoConverter dtoConverter =
            DtoConverterFactory.getConverter(Withdrawal.class, WithdrawalDto.class);
    {
        /* ──────────────────────── For test ──────────────────────────── */
        log.trace("WithdrawalService dtoConverter : {}", dtoConverter.getClass());

    }

    /* ──────────────────────── MEMBER WITHDRAWAL CRUD ──────────────────────── */

    /**
     * 모든 탈퇴원을 데이터베이스에서 가져옵니다.
     *
     * @author Claire j
     * @since JDK 17
     *
     * @return 멤버 탈퇴 정보 리스트
     */
    public List<DataTransferObject> getAllWithdrawals(){
        List<DataTransferObject> dtoList =
                dtoConverter.convertToDtoList(withdrawalRepository.findAll());
        return dtoList;
    }

    /**
     * 탈퇴 ID로 탈퇴원 정보를 조회합니다.
     *
     * @author Claire j
     * @since JDK 17
     *
     * @param withdrawalId
     * @return ServiceResult 객체. 멤버 탈퇴 정보 또는 실패 메시지를 포함.
     */
    public ServiceResult getWithdrawalById(String withdrawalId) {
        DataTransferObject dto =
                dtoConverter.convertToDto(withdrawalRepository.findByWithdrawalId(withdrawalId));

        if (dto == null) return ServiceResult.fail("Withdrawal not found");
        return ServiceResult.success(dto);
    }

    /**
     * 새로운 탈퇴원 정보를 데이터베이스에 생성합니다.
     *
     * @author Claire j
     * @since JDK 17
     *
     * @param withdrawalCreationRequestBody
     * @return ServiceResult 객체. 생성된 탈퇴원의 ID 또는 실패 메시지를 포함.
     */
    public ServiceResult insertWithdrawal(WithdrawalCreationRequestBody withdrawalCreationRequestBody) {
        if(withdrawalCreationRequestBody.hasNullField())
            return ServiceResult.fail("Withdrawal field is null");

        String generateWithdrawalId = RandomUUIDGenerator.generate();

        Withdrawal withdrawal = Withdrawal.builder()
                .withdrawalId(generateWithdrawalId)
                .reason(withdrawalCreationRequestBody.getReason())
                .createdAt(LocalDateTime.now())
                .build();
        log.info("WithdrawalService insertWithdrawal : {}", withdrawal);

        withdrawalRepository.save(withdrawal);
        return ServiceResult.success(generateWithdrawalId);
    }

    /**
     * 탈퇴원 ID로 탈퇴원 정보를 삭제합니다.
     *
     * @author Claire j
     * @since JDK 17
     *
     * @param withdrawalId
     * @return ServiceResult 객체. 삭제된 탈퇴원의 ID 또는 실패 메시지를 포함.
     */
    public ServiceResult deleteWithdrawalById(String withdrawalId) {
        if(withdrawalRepository.findByWithdrawalId(withdrawalId) == null)
            return ServiceResult.fail("Withdrawal not found");

        withdrawalRepository.deleteByWithdrawalId(withdrawalId);
        return ServiceResult.success(withdrawalId);
    }
}
