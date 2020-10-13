package com.fpt.gta.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.fpt.gta.MainActivity;
import com.fpt.gta.R;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;

import static com.fpt.gta.App.CHANNEL_ID;


public class NotificationService extends BroadcastReceiver {

    private String type = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(type);
        builder.setTicker("Message Alert");
        builder.setSmallIcon(R.mipmap.notify);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        notificationManager.notify(200, builder.build());
    }
}
