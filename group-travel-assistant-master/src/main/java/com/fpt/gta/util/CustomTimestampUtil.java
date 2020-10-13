package com.fpt.gta.util;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

public class CustomTimestampUtil {
    public static Timestamp updateByHours(Timestamp time, int hour) {
        return Timestamp.from(time.toInstant().plus(hour, ChronoUnit.HOURS));
    }

    public static Timestamp updateByMinutes(Timestamp time, int minute) {
        return Timestamp.from(time.toInstant().plus(minute, ChronoUnit.MINUTES));
    }
}
