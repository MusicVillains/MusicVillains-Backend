package com.teamseven.MusicVillain.Dto.Converter;

import com.teamseven.MusicVillain.Dto.FeedDto;
import com.teamseven.MusicVillain.Feed.Feed;

public class DtoConverterFactory {
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceClass, Class<T> targetClass) {

        if (sourceClass == Feed.class && targetClass == FeedDto.class) {
            return (Converter<S, T>) new FeedDtoConverter();
//        } else if (sourceClass == Member.class && targetClass == MemberDto.class) {
//            return (Converter<S, T>) new MemberDtoConverter();
//        } else if (sourceClass == Notification.class && targetClass == NotificationDto.class) {
//            return (Converter<S, T>) new NotificationDtoConverter();
        } else {
            throw new IllegalArgumentException("No suitable converter found.");
        }
    }
}
