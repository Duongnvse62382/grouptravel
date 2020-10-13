package com.fpt.gta.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    /*
     * convert date by iso8601
     * */
    public static String convertDate(String date, String format) {

        try {
            //get follow format
            //format for iso8601
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            //parse iso8601 => date
            Date dateNew = dateformat.parse(date);
            //format following 02:12:22, Mon,12/06/2018(example)
            DateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(dateNew);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String uppercaseFirstLetter(String example){
        String result = example.substring(0,1).toUpperCase() + example.substring(1).toLowerCase();
        return result;
    }
}
