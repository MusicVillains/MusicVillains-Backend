package com.teamseven.MusicVillain.Dto.Converter;

import com.teamseven.MusicVillain.Dto.*;
import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Notification.Notification;
import com.teamseven.MusicVillain.Withdrawal.Withdrawal;
@SuppressWarnings("unchecked")
public class DtoConverterFactory {
    public static <S, T extends DataTransferObject> DtoConverter<S, T> getConverter(Class<S> sourceClass, Class<? extends T> targetClass) {

        if (sourceClass == Feed.class && targetClass == FeedDto.class) {
            return (DtoConverter<S, T>) new FeedDtoConverter();
        } else if (sourceClass == Member.class && targetClass == MemberDto.class) {
            return (DtoConverter<S, T>) new MemberDtoConverter();
        } else if (sourceClass == Withdrawal.class && targetClass == WithdrawalDto.class) {
            return (DtoConverter<S, T>) new WithdrawalDtoConverter();
        } else if (sourceClass == Notification.class && targetClass == NotificationDto.class) {
            return (DtoConverter<S, T>) new NotificationDtoConverter();
        } else {
            throw new IllegalArgumentException("No suitable converter found.");
        }
    }
}
