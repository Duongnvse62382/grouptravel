package com.fpt.gta;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

public class App extends Application {
    private static Resources resources;
    private static Context context;
    public static final  String CHANNEL_ID = "com.fpt.gta";
    @Override
    public void onCreate() {
        super.onCreate();

        resources = getResources();
        context=getApplicationContext();
        createNotificationChannel();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public static Resources getAppResources() {
        return resources;
    }
    public static Context getAppContext() {
        return App.context;
    }
}
