package com.fpt.gta.util;

import us.dustinj.timezonemap.TimeZoneMap;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class GeometryUtil {
    private static TimeZoneMap timeZoneMap;

    static {
        timeZoneMap = TimeZoneMap.
                forRegion(-90,
                        -180,
                        90,
                        180);
    }

    public static String getTimeZoneFromLatLng(double lat, double lng) {
        return timeZoneMap.getOverlappingTimeZone(lat, lng).getZoneId();
    }

    public static String getTimeZoneFromLatLng(BigDecimal lat, BigDecimal lng) {
        return timeZoneMap.getOverlappingTimeZone(lat.doubleValue(), lng.doubleValue()).getZoneId();
    }

    public static BigDecimal latitudeDecimalOf(double lat) {
        return BigDecimal.valueOf(lat).setScale(6, RoundingMode.DOWN);
    }

    public static BigDecimal longitudeDecimalOf(double lng) {
        return BigDecimal.valueOf(lng).setScale(6, RoundingMode.DOWN);
    }
}
