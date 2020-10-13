package com.fpt.gta.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public final class ZonedDateTimeUtil {

    public static Date convertDateFromUtcToTimeZone(Date utcDate, String timeZone) {
        return convertBetweenTwoTimeZone(utcDate, "UTC", timeZone);
    }

    public static Date convertDateFromTimeZoneToUtc(Date date, String timeZone) {
        return convertBetweenTwoTimeZone(date, timeZone, "UTC");
    }

    public static Date convertBetweenTwoTimeZone(Date date, String fromTimeZone, String toTimeZone) {
//        prepare Zone Object
        ZoneId fromTimeZoneId = ZoneId.of(fromTimeZone);
        ZoneId toTimeZoneId = ZoneId.of(toTimeZone);

//        prepare DateTime object
//        prepare Datetime With Zone Object
        ZonedDateTime fromZonedDateTime =
                ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).withZoneSameLocal(fromTimeZoneId);

//        get Zone datetime at UTC
        ZonedDateTime toZonedDateTime = fromZonedDateTime
                .withZoneSameInstant(toTimeZoneId)
                .withZoneSameLocal(ZoneId.systemDefault());

        return Date.from(toZonedDateTime.toInstant());
    }

    public static String convertDateFromUtcToTimeZoneString(Date utcDate, String timeZone) {
        //        prepare Zone Object
        ZoneId utcTimeZone = ZoneId.of("UTC");
        ZoneId destinationTimeZone = ZoneId.of(timeZone);

        //        prepare DateTime object
        ZonedDateTime utcDatetime = ZonedDateTime.ofInstant(utcDate.toInstant(), utcTimeZone);

        //        get Zone datetime at UTC
        ZonedDateTime zonedDateTimeUTC = utcDatetime.withZoneSameLocal(destinationTimeZone);
        return Date.from(zonedDateTimeUTC.toInstant()).toString();
    }

    public static String convertDateTimeToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return simpleDateFormat.format(date);
    }

    public static String convertDateToStringASIA(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(date);
    }

    public static String convertDateTimeHmsToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return simpleDateFormat.format(date);
    }


    public static String convertDateToStringTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(date);
    }

    public static String convertDateToStringTimeSS(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(date);
    }


    public static String convertTimeToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(date);
    }

    public static Date convertStringToDateOrTime(String dateString) {
        String[] acceptedFormats = {"dd-MM-yyyy HH:mm:ss", "dd-MM-yyyy HH:mm", "dd-MM-yyyy", "HH:mm"};
        try {
            return DateUtils.parseDate(dateString, acceptedFormats);
        } catch (ParseException e) {
            throw new RuntimeException("Parse date Error");
        }
    }


    public static boolean compareTwoDate(Date firstDate, Date secondDate) {
        return ZonedDateTimeUtil.convertDateToStringASIA(firstDate)
                .equals(
                        ZonedDateTimeUtil.convertDateToStringASIA(secondDate)
                );
    }

}
