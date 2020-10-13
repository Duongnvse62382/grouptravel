package com.fpt.gta.util;

import android.app.Activity;

public final class ActivityUtil {
    public static boolean isRunning(Activity activity) {
        boolean result = false;
        if (activity != null) {
            if (!(activity.isFinishing() || activity.isDestroyed())) {
                result = true;
            }
        }
        return result;
    }
}
