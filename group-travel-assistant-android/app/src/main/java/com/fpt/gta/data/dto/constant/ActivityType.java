package com.fpt.gta.data.dto.constant;

public final class ActivityType {
    public static final Integer ACTIVITY = 1;
    public static final Integer TRANSPORTATION = 2;
    public static final Integer ACCOMMODATION = 3;
    public static final Integer FOODANDBEVERAGE = 4;

    public static String of(Integer idType) {
        if (idType.equals(1)) {
            return "Activity";
        } else if (idType.equals(2)) {
            return "Transportation";
        } else if (idType.equals(3)) {
            return "Accommodation";
        } else if (idType.equals(4)) {
            return "Food And Beverage";
        }
        return "";
    }
}
