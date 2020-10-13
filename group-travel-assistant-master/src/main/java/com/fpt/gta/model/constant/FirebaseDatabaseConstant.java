package com.fpt.gta.model.constant;

import java.text.MessageFormat;

public final class FirebaseDatabaseConstant {

    private static String reloadPlanPath = "{0}/listener/reloadPlan";
    private static String reloadReadyPath = "{0}/listener/reloadReady";
    private static String reloadGroupStatusPath = "{0}/listener/reloadGroupStatus";
    private static String reloadTransactionUtcPath = "{0}/listener/reloadTransactionUtc";
    private static String reloadMemberBadgesPath = "{0}/listener/reloadMemberBadges";
    private static String reloadMemberLatestReadMessagePath = "{0}/members/{1}/latestReadMessage";
    private static String chatMessagePath = "{0}/messages/{1}";


    public static String getReloadPlanPath(Integer idGroup) {
        return MessageFormat.format(reloadPlanPath, idGroup);
    }

    public static String getReloadReadyPath(Integer idGroup) {
        return MessageFormat.format(reloadReadyPath, idGroup);
    }

    public static String getReloadGroupStatusPath(Integer idGroup) {
        return MessageFormat.format(reloadGroupStatusPath, idGroup);

    }

    public static String getReloadTransactionUtcPath(Integer idGroup) {
        return MessageFormat.format(reloadTransactionUtcPath, idGroup);

    }

    public static String getReloadMemberBadgesPath(Integer idGroup) {
        return MessageFormat.format(reloadMemberBadgesPath, idGroup);
    }

    public static String getReloadMemberLatestReadMessagePath(Integer idGroup, String firebaseUid) {
        return MessageFormat.format(reloadMemberLatestReadMessagePath, idGroup, firebaseUid);
    }

    public static String getChatMessagePath(Integer idGroup, String uuid) {
        return MessageFormat.format(chatMessagePath, idGroup, uuid);
    }
}
