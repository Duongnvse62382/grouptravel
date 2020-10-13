package com.fpt.gta.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class ZonedDateTimeUtil {

    public static Timestamp convertToUtcWithTimeZone(Timestamp timestamp, String timeZone) {
        return convertBetweenTwoTimeZone(timestamp, timeZone, "UTC");
    }

    public static Timestamp convertFromUtcToTimeZone(Timestamp timestamp, String timeZone) {

        return convertBetweenTwoTimeZone(timestamp, "UTC", timeZone);
    }

    public static Timestamp convertBetweenTwoTimeZone(Timestamp timestamp, String fromTimeZone, String toTimeZone) {

//        prepare Zone Object
        ZoneId fromTimeZoneId = ZoneId.of(fromTimeZone);
        ZoneId toTimeZoneId = ZoneId.of(toTimeZone);

//        prepare DateTime object
        LocalDateTime localDateTimeBeforeDST = timestamp.toLocalDateTime();
//        prepare Datetime With Zone Object
        ZonedDateTime fromZonedDateTime = ZonedDateTime.of(localDateTimeBeforeDST, fromTimeZoneId);

//        get Zone datetime at UTC
        ZonedDateTime toZonedDateTime = fromZonedDateTime.withZoneSameInstant(toTimeZoneId);

        return Timestamp.valueOf(toZonedDateTime.toLocalDateTime());
    }
}
