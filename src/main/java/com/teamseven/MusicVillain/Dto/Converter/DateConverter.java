package com.teamseven.MusicVillain.Dto.Converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateConverter {
    public static LocalDateTime convertDateToLocalDateTime(Date date){
        LocalDateTime localDateTime = date.toInstant() // Date to Instant
                .atZone(ZoneId.systemDefault()) // Instant to ZonedDateTime
                .toLocalDateTime(); // ZonedDateTime to LocalDateTime
        return localDateTime;
    }

}
