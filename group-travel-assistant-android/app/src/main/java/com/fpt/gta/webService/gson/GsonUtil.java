package com.fpt.gta.webService.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {
    public static Gson getGson(){
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setExclusionStrategies(new AnnotationExclusionStrategy())
                .create();
    }
}
