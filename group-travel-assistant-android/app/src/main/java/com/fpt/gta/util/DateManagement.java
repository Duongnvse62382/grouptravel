package com.fpt.gta.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateManagement {
    public static String convertDayOfWeek(int dayOfWeek) {
        String convert = "";
        switch (dayOfWeek) {
            case 1:
                convert = "Chủ Nhật";
                break;
            case 2:
                convert = "Thứ 2";
                break;
            case 3:
                convert = "Thứ 3";
                break;
            case 4:
                convert = "Thứ 4";
                break;
            case 5:
                convert = "Thứ 5";
                break;
            case 6:
                convert = "Thứ 6";
                break;
            case 7:
                convert = "Thứ 7";
                break;
        }
        return convert;
    }

    public static String fortmatIntToDate(int date) {
        String day = "";
        if (date < 10) {
            day = "0" + String.valueOf(date);
        } else {
            day = String.valueOf(date);
        }
        return day;
    }

    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, -1);
        return cal.getTime();
    }

    public static String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
        return dateFormat.format(yesterday());
    }

    public static String getTodayDateString() {
        DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
        return dateFormat.format(today());
    }
    public static boolean isDateAfter(String startDate, String endDate)
    {
        try
        {
            String myFormatString = "dd/MM/yyyy"; // for example
            SimpleDateFormat df = new SimpleDateFormat (myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);
            if (date1.after(startingDate))
                return true;
            else
                return false;
        }
        catch (Exception e)
        {

            return false;
        }
    }
    public static Date today() {
        final Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, 0);
        return cal.getTime();
    }

    public static Date getStartPreviosMonth() {
        final Calendar cal = Calendar.getInstance();
        cal.set( Calendar.DATE, 1);
        cal.add( Calendar.MONTH, -1);
        return cal.getTime();
    }

    public static Date getEndPreviosMonth() {
        final Calendar cal = Calendar.getInstance();
        cal.add( Calendar.MONTH, -1);
        int dayOfPreviosMonth = cal.getActualMaximum( Calendar.DAY_OF_MONTH);
        cal.set( Calendar.DATE, dayOfPreviosMonth);
        return cal.getTime();
    }

    public static String getStartPreviosMonthDateString() {
        DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
        return dateFormat.format(getStartPreviosMonth());
    }

    public static String getEndPreviosMonthDateString() {
        DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
        return dateFormat.format(getEndPreviosMonth());
    }

    public static Date getStartThisMonth() {
        final Calendar cal = Calendar.getInstance();
        cal.set( Calendar.DATE, 1);
        cal.add( Calendar.MONTH, 0);
        return cal.getTime();
    }

    public static Date getEndThisMonth() {
        final Calendar cal = Calendar.getInstance();
        cal.add( Calendar.MONTH, 0);
        int dayOfPreviosMonth = cal.getActualMaximum( Calendar.DAY_OF_MONTH);
        int dayNow = cal.get( Calendar.DATE);
        if (dayOfPreviosMonth >= dayNow) {
            dayOfPreviosMonth = dayNow-1;
        }
        cal.set( Calendar.DATE, dayOfPreviosMonth);
        return cal.getTime();
    }

    public static String getStartThisMonthDateString() {
        DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
        return dateFormat.format(getStartThisMonth());
    }

    public static String getEndThisMonthDateString() {
        DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
        return dateFormat.format(getEndThisMonth());
    }

    public static String getToday() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get( Calendar.DAY_OF_WEEK);
        String dayWeek = convertDayOfWeek(dayOfWeek);
        String today = dayWeek + ", " + getTodayDateString();
        return today;
    }

    public static String getYesterDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get( Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 8;
        }
        String dayWeek = convertDayOfWeek(dayOfWeek);

        String today = dayWeek + ", " + getYesterdayDateString();
        return today;
    }

    public static Date getStartPreviosWeek() {
        final Calendar cal = Calendar.getInstance();
        cal.add( Calendar.WEEK_OF_YEAR, -1);
        cal.set( Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return cal.getTime();
    }

    public static Date getEndPreviosWeek() {
        final Calendar cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add( Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    public static String getStartPreviosWeekDateString() {
        DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
        return dateFormat.format(getStartPreviosWeek());
    }

    public static String getEndPreviosWeekDateString() {
        DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
        return dateFormat.format(getEndPreviosWeek());
    }

    public static Date getStartThisWeek() {
        final Calendar cal = Calendar.getInstance();
        cal.add( Calendar.WEEK_OF_YEAR, 0);
        cal.set( Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return cal.getTime();
    }


    public static String getStartThisWeekDateString() {
        DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
        return dateFormat.format(getStartThisWeek());
    }

    public static String getEndThisWeekDateString() {
        DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
        final Calendar cal = Calendar.getInstance();
        int endDay = cal.get( Calendar.DATE-1);
        int endOfThisWeek = cal.getFirstDayOfWeek();
        if(endDay<=endOfThisWeek){
            return dateFormat.format(cal.getTime());
        }else {
            return dateFormat.format(yesterday());
        }
    }
    public static String formatXAxisCombineChart(String time){
        String timeFormat="";
        String tmp[] = time.split("/");
        timeFormat=tmp[0]+"/"+tmp[1];

        return timeFormat;
    }
    public static  float numberOfDayBetween(String dateGo,String dateEnd){
        float daysBetween =0;
        DateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        try {
            Date dateBefore = myFormat.parse(dateGo);
            Date dateAfter = myFormat.parse(dateEnd);
            long difference = dateAfter.getTime() - dateBefore.getTime();
            daysBetween = (difference / (1000*60*60*24));
            /* You can also convert the milliseconds to days using this method
             * float daysBetween =
             *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return daysBetween + 1;
    }
//    public static Date changeStringtoDate(String stringDate){
//
//        Date  dateBefore = null;
//        DateFormat myFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
//
//        try {
//             dateBefore = myFormat.parse(stringDate);
//        } catch (ParseException e) {
//            e.printStackTrace ();
//        }
//        return dateBefore;
//    }

    public static Date changeStringtoDate(String stringDate){

        Date  dateBefore = null;
        DateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);

        try {
            dateBefore = myFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        return dateBefore;
    }


}
