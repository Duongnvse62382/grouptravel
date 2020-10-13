package com.fpt.gta.data.dto.constant;

public final class GroupStatus {
//    public static final Integer INACTIVE = 0;
//    public static final Integer ACTIVE = 1;
    public static final Integer PLANNING = 1;
    public static final Integer PENDING = 2;
    public static final Integer EXECUTING = 3;

    public static String of(Integer idType) {
        if (idType.equals(PLANNING)) {
            return "Planning";
        } else if (idType.equals(PENDING)) {
            return "Pending";
        } else if (idType.equals(EXECUTING)) {
            return "Executing";
        }
        return "";
    }
}
