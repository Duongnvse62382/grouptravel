package com.fpt.gta.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtils {

    public static void saveStringSharedPreference(Context context, String key, String value) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                    context.getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.commit();
        }catch (Exception e){

        }
    }

    public static String getStringSharedPreference(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        String defaultValue = "";
        return prefs.getString(key, defaultValue);
    }

    public static void removeStringSharedPreference(Context context, String key) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                    context.getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            editor.commit();
        }catch (Exception e){

        }
    }

    public static void saveIntSharedPreference(Context context, String key, int value) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                    context.getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(key, value);
            editor.commit();
        }catch (Exception e){

        }
    }

    public static int getIntSharedPreference(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        int defaultValue = 0;
        return prefs.getInt(key, defaultValue);
    }

    public static void removeIntSharedPreference(Context context, String key) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                    context.getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            editor.commit();
        }catch (Exception e){

        }
    }

    public static void saveLongSharedPreference(Context context, String key, long value) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                    context.getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(key, value);
            editor.commit();
        }catch (Exception e){

        }
    }

    public static long getLongSharedPreference(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        long defaultValue = 0;
        return prefs.getLong(key, defaultValue);
    }

    public static void removeLongSharedPreference(Context context, String key) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                    context.getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            editor.commit();
        }catch (Exception e){

        }
    }


    public static void saveBooleanSharedPreference(Context context, String key, boolean value) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                    context.getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean getBooleanSharedPreference(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        boolean defaultValue = false;
        return prefs.getBoolean(key, defaultValue);
    }

    public static void removeBooleanSharedPreference(Context context, String key) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                    context.getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
