package com.fpt.gta.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kaopiz.kprogresshud.KProgressHUD;


public class KProgressHUDManager {
    public static KProgressHUD showProgressBar(Context context) {
        try {
            Activity activity = (Activity) context;
            if (!(activity.isFinishing() || activity.isDestroyed())) {
                KProgressHUD hud = KProgressHUD.create(context)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();

                activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                    }

                    @Override
                    public void onActivityStarted(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivityResumed(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivityPaused(@NonNull Activity activity) {
                        try {
                            if (hud != null && hud.isShowing()) {
                                if (!(activity.isFinishing() || activity.isDestroyed())) {
                                    hud.dismiss();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onActivityStopped(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                    }

                    @Override
                    public void onActivityDestroyed(@NonNull Activity activity) {

                    }
                });

                return hud;
            }
        } catch (Exception e) {
            Log.e("Khub", "showProgressBar: khub error");
        }
        return null;
    }

    public static void dismiss(Context context, KProgressHUD hud) {
        try {
            if (hud != null && hud.isShowing()) {
                Activity activity = (Activity) context;
                if (!(activity.isFinishing() || activity.isDestroyed())) {
                    hud.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
