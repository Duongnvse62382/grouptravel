package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Activity;
import com.fpt.gta.model.entity.Document;

import java.util.List;

public interface ActivityService {

    Activity createActivityWithDocument(String firebaseUid, Integer idPlan, Activity activity);

    Activity createActivityWithDocument(String firebaseUid, Integer idPlan, Activity activity, List<Document> newDocumentList);

    Activity updateActivityWithDocument(String firebaseUid, Integer idPlan, Activity activity);

    Activity updateActivityWithDocument(String firebaseUid, Integer idPlan, Activity activity, List<Document> newDocumentList);

    void deleteActivity(String firebaseUid, Integer idActivity);

    List<Activity> findAllActivityInPlan(String firebaseUid, Integer idPlan);

    Activity findActivityById(String firebaseUid, Integer idActivity);

    Activity getActivityById(Integer idActivity);
}
