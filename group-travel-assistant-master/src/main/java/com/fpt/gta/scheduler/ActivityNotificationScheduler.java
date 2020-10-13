package com.fpt.gta.scheduler;

import com.fpt.gta.model.entity.Activity;
import com.fpt.gta.model.entity.Group;
import com.fpt.gta.model.entity.Trip;
import com.fpt.gta.model.repository.ActivityRepository;
import com.fpt.gta.model.service.MessagingService;
import com.fpt.gta.util.ZonedDateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class ActivityNotificationScheduler {

    ActivityRepository activityRepository;
    MessagingService messagingService;

    @Autowired
    public ActivityNotificationScheduler(ActivityRepository activityRepository, MessagingService messagingService) {
        this.activityRepository = activityRepository;
        this.messagingService = messagingService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void notifyStartActivity() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date nowUtc = new Date();

        nowUtc = simpleDateFormat.parse(simpleDateFormat.format(nowUtc));
        nowUtc = ZonedDateTimeUtil.convertToUtcWithTimeZone(new Timestamp(nowUtc.getTime()), ZoneId.systemDefault().toString());
        Date afterUtc = new Date(nowUtc.getTime() + 60 * 1000);

        //15min
        nowUtc = new Date(nowUtc.getTime() + 15 * 60 * 1000);
        afterUtc = new Date(afterUtc.getTime() + 15 * 60 * 1000);
        System.out.println("scheduled notify start from "+nowUtc+" to "+afterUtc);
        List<Activity> activityStartNotifyList = activityRepository.findAllActivityStartUtcBetweenUtcDate(nowUtc, afterUtc);
        System.out.println("start "+activityStartNotifyList.size());
        for (int i = activityStartNotifyList.size() - 1; i >= 0; i--) {
            Activity activity = activityStartNotifyList.get(i);
            Trip trip = activity.getPlan().getTrip();
            LocalDateTime tripStartUtcAt = trip.getStartUtcAt().toLocalDateTime();
            LocalDateTime tripEndUtcAt = trip.getEndUtcAt().toLocalDateTime();
            if (activity.getStartUtcAt().toLocalDateTime().isBefore(tripStartUtcAt)
                    &&
                    activity.getStartUtcAt().toLocalDateTime().isAfter(tripEndUtcAt)) {
                activityStartNotifyList.remove(i);
            }
        }

        for (Activity activity : activityStartNotifyList) {
            Group group = activity.getPlan().getTrip().getGroup();
            Map<String, String> data = new HashMap<>();
            data.put("messageType", "activity");
            data.put("isStarting", "true");

            data.put("idActivity", activity.getId().toString());
            data.put("idGroup", activity.getPlan().getTrip().getGroup().getId().toString());
            data.put("idType", activity.getIdType().toString());
            data.put("name", activity.getName());
            data.put("startPlaceName", activity.getStartPlace().getName());
            data.put("endPlaceName", activity.getEndPlace().getName());

            messagingService.messageAllInGroupAsync(group.getId(), data);
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void notifyEndActivity() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date nowUtc = new Date();

        nowUtc = simpleDateFormat.parse(simpleDateFormat.format(nowUtc));
        nowUtc = ZonedDateTimeUtil.convertToUtcWithTimeZone(new Timestamp(nowUtc.getTime()), ZoneId.systemDefault().toString());
        Date afterUtc = new Date(nowUtc.getTime() + 60 * 1000);

        //15min
        nowUtc = new Date(nowUtc.getTime() + 15 * 60 * 1000);
        afterUtc = new Date(afterUtc.getTime() + 15 * 60 * 1000);

        List<Activity> activityEndNotifyList = activityRepository.findAllActivityEndUtcBetweenUtcDate(nowUtc, afterUtc);
        for (int i = activityEndNotifyList.size() - 1; i >= 0; i--) {
            Activity activity = activityEndNotifyList.get(i);
            Trip trip = activity.getPlan().getTrip();
            LocalDateTime tripStartUtcAt = trip.getStartUtcAt().toLocalDateTime();
            LocalDateTime tripEndUtcAt = trip.getEndUtcAt().toLocalDateTime();
            if (activity.getEndUtcAt().toLocalDateTime().isBefore(tripStartUtcAt)
                    &&
                    activity.getEndUtcAt().toLocalDateTime().isAfter(tripEndUtcAt)) {
                activityEndNotifyList.remove(i);
            }
        }

        for (Activity activity : activityEndNotifyList) {
            Group group = activity.getPlan().getTrip().getGroup();
            Map<String, String> data = new HashMap<>();
            data.put("messageType", "activity");
            data.put("isStarting", "false");

            data.put("idActivity", activity.getId().toString());
            data.put("idGroup", activity.getPlan().getTrip().getGroup().getId().toString());
            data.put("idType", activity.getIdType().toString());
            data.put("name", activity.getName());
            data.put("startPlaceName", activity.getStartPlace().getName());
            data.put("endPlaceName", activity.getEndPlace().getName());

            messagingService.messageAllInGroupAsync(group.getId(), data);
        }
    }

}
