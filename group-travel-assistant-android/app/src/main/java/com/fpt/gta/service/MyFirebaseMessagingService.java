package com.fpt.gta.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fpt.gta.App;
import com.fpt.gta.BaseActivity;
import com.fpt.gta.MainActivity;
import com.fpt.gta.R;
import com.fpt.gta.data.dto.constant.ActivityType;
import com.fpt.gta.ReloadActivity;
import com.fpt.gta.data.dto.constant.GroupStatus;
import com.fpt.gta.feature.notifyactivity.ActivityNotifyActivity;
import com.fpt.gta.util.GTABundle;
import com.fpt.gta.util.SharePreferenceUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Map;

import static com.fpt.gta.App.CHANNEL_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        String messageType = data.get("messageType");
        if (messageType.equals("activity")) {
            Integer idActivity = Integer.parseInt(data.get("idActivity"));
            Boolean isStarting = Boolean.valueOf(data.get("isStarting"));
            String name = data.get("name");
            Integer idGroup = Integer.parseInt(data.get("idGroup"));
            Integer idType = Integer.parseInt(data.get("idType"));
            String startPlaceName = data.get("startPlaceName");
            String endPlaceName = data.get("endPlaceName");

            Intent notificationIntent = new Intent(App.getAppContext(), MainActivity.class);
            notificationIntent.putExtra(GTABundle.IDACTIVITYNOTIFY, idActivity);
            notificationIntent.putExtra(GTABundle.IDGROUPNOTIFY, idGroup);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(App.getAppContext(), (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getAppContext());
            NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getAppContext(), CHANNEL_ID);
            builder.setContentTitle(ActivityType.of(idType));
            if(isStarting){
                builder.setContentText("Starting: "+ name + " in " + startPlaceName);
            }else {
                builder.setContentText("Ending: " + name + " in " + startPlaceName);
            }

            builder.setTicker("Message Alert");
            builder.setSmallIcon(R.mipmap.notify);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            notificationManager.notify(200, builder.build());
        } else if (messageType.equals("groupStatus")) {
            Integer idGroup = Integer.parseInt(data.get("idGroup"));
            Integer idStatus = Integer.parseInt(data.get("idStatus"));
            String name = data.get("name");
            Intent notificationIntent = new Intent(App.getAppContext(), MainActivity.class);
            notificationIntent.putExtra(GTABundle.IDGROUPPLANING, idGroup);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(App.getAppContext(), (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getAppContext());
            NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getAppContext(), CHANNEL_ID);
            builder.setContentTitle(GroupStatus.of(idStatus));
            builder.setContentText("Journey: " + name + " is " + GroupStatus.of(idStatus) + ". Please take a look");
            builder.setTicker("Message Alert");
            builder.setSmallIcon(R.mipmap.notify);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            notificationManager.notify(200, builder.build());
        }

    }
}
