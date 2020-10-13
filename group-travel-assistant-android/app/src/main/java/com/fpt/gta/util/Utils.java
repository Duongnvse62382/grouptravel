package com.fpt.gta.util;

import android.util.Patterns;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static boolean isValidData(String data) {
        boolean check= false;
        if (data != null && !data.isEmpty()) {
            check = true;
        }
        return check;
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static boolean isValidPhoneNumber(String phone)
    {
        if (!phone.trim().equals("") || phone.length() > 10)
        {
            return Patterns.PHONE.matcher(phone).matches();
        }

        return false;
    }

    public static String formatString(String data){
        String result= "";
        if(data!= null){
            result= data;
        }
        return result;
    }

    public static String convertDateToSubmit(String data){
        String[] tmp= data.split(" ");
        String result="";
        if(tmp[0]!= null){
            String[] date = tmp[0].split("-");
            result=date[2]+"-"+date[1]+"-"+date[0];
        }
        result+=" "+ tmp[1];
        return result;
    }

    public static Timestamp convertStringToTimestamp(String data){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(data);
            return new Timestamp(parsedDate.getTime());
        } catch(Exception e) { //this generic but you can control another types of exception
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkValidDate(Timestamp from, Timestamp to){
        return from.before(to) ;
    }

    public static float calculateTimeDifferences(Timestamp from, Timestamp to){
        long difference = from.getTime() - to.getTime();
        return  (difference / (1000*60*60*24));
    }

    public static String formatStringTime(int num) {
        if(num<10){
            return "0"+num;
        }else {
            return ""+num;
        }
    }

    public static String convertTimeToString(String time){
        String tmp1[] = time.split(" ");
        String dateSplit[] = tmp1[0].split("-");
        return tmp1[1]+" " + dateSplit[2] +"-"+dateSplit[1]+"-"+dateSplit[0];
    }
}
