package com.fpt.gta.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class DateUtil {
    public static int parseDayOfWeek(String day, Locale locale)
            throws ParseException {
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", locale);
        Date date = dayFormat.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }
    public static long getDateDiff(Timestamp oldTs, Timestamp newTs, TimeUnit timeUnit) {
        if (newTs.compareTo(oldTs) < 0) {
            Timestamp tmp = oldTs;
            oldTs = newTs;
            newTs = tmp;
        }
        long diffInMS = newTs.getTime() - oldTs.getTime();
        return timeUnit.convert(diffInMS, TimeUnit.MILLISECONDS);
    }
    public static long getActualDateDiff(Timestamp oldTs, Timestamp newTs) {
       return ChronoUnit.DAYS.between(oldTs.toLocalDateTime().toLocalDate(), newTs.toLocalDateTime().toLocalDate());
    }
}
