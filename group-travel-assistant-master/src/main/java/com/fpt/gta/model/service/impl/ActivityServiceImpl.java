package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.NotFoundException;
import com.fpt.gta.model.constant.PlanStatus;
import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.repository.ActivityRepository;
import com.fpt.gta.model.service.*;
import com.fpt.gta.util.ZonedDateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    ActivityRepository activityRepository;
    TripService tripService;
    PlaceService placeService;
    PersonService personService;
    MemberService memberService;
    PlanService planService;
    DocumentService documentService;
    AuthenticationService authenticationService;
    ForkJoinPool forkJoinPool;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, TripService tripService, PlaceService placeService, PersonService personService, MemberService memberService, PlanService planService, DocumentService documentService, AuthenticationService authenticationService, ForkJoinPool forkJoinPool) {
        this.activityRepository = activityRepository;
        this.tripService = tripService;
        this.placeService = placeService;
        this.personService = personService;
        this.memberService = memberService;
        this.planService = planService;
        this.documentService = documentService;
        this.authenticationService = authenticationService;
        this.forkJoinPool = forkJoinPool;
    }

    @Override
    public Activity createActivityWithDocument(String firebaseUid, Integer idPlan, Activity activity) {
        Plan plan = planService.getPlanById(idPlan);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        authenticationService.checkJoinedGroupByFirebaseUid(firebaseUid, plan.getTrip().getGroup().getId());

        if (plan.getIdStatus().equals(PlanStatus.ELECTED)) {
            authenticationService.checkTripOwner(person.getId(), plan.getTrip().getId());
        } else {
            authenticationService.checkPlanOwner(person.getId(), plan.getId());
        }
        Trip trip = plan.getTrip();
        Integer idTrip = trip.getId();
        Activity newActivity = new Activity();

        mapProperties(activity, newActivity);

        newActivity.setPlan(plan);
        newActivity = activityRepository.save(newActivity);

//        if (idTrip > 0) {
//            Place tripCity = trip.getEndPlace();
//            activity.setIsTooFar(placeService
//                    .checkTravelTimeBetweenPlaces(tripCity, newActivity.getStartPlace()));
//        }

        return newActivity;
    }

    @Override
    public Activity createActivityWithDocument(String firebaseUid, Integer idPlan, Activity activity, List<Document> newDocumentList) {
        Activity createdActivity = createActivityWithDocument(firebaseUid, idPlan, activity);
        if (newDocumentList != null) {
//                newDocumentList.parallelStream().parallel().forEach(document -> {
//
//                });
            for (Document document : newDocumentList) {
                boolean isNewDocument = true;
                if (document.getId() != null) {
                    if (document.getId() != 0) {
                        isNewDocument = false;
                    }
                }

                if (isNewDocument) {
                    documentService.addNewDocumentActivity(
                            firebaseUid,
                            createdActivity.getId(),
                            document.getContentType(),
                            document.getUri()
                    );
                } else {
                    documentService.addGroupDocumentToActivity(
                            firebaseUid,
                            createdActivity.getId(),
                            document.getId()
                    );
                }
            }
        }
        return createdActivity;
    }

    @Override
    public Activity updateActivityWithDocument(String firebaseUid, Integer idPlan, Activity activity) {
        Plan plan = planService.getPlanById(idPlan);
        Activity oldActivity = getActivityById(activity.getId());
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        authenticationService.checkJoinedGroupByFirebaseUid(firebaseUid, plan.getTrip().getGroup().getId());

        if (plan.getIdStatus().equals(PlanStatus.ELECTED)) {
            authenticationService.checkTripOwner(person.getId(), plan.getTrip().getId());
        } else {
            authenticationService.checkPlanOwner(person.getId(), plan.getId());
        }
        Trip trip = plan.getTrip();
        Integer idTrip = trip.getId();

        mapProperties(activity, oldActivity);
        oldActivity = activityRepository.save(oldActivity);
//        if (idTrip > 0) {
//            Place tripCity = trip.getEndPlace();
//            activity.setIsTooFar(placeService
//                    .checkTravelTimeBetweenPlaces(tripCity, oldActivity.getStartPlace()));
//        }
        return oldActivity;
    }

    @Override
    public Activity updateActivityWithDocument(String firebaseUid, Integer idPlan, Activity activity, List<Document> uploadDocumentList) {
        Activity oldActivity = updateActivityWithDocument(firebaseUid, idPlan, activity);
        List<Document> oldDocumentList = documentService.findAllDocumentActivity(firebaseUid, activity.getId());
        if (uploadDocumentList != null) {
//            forkJoinPool.submit(() -> {
//                uploadDocumentList.parallelStream().parallel().forEach(newDocument -> {
//
//                });
//            });
            for (Document uploadDocument : uploadDocumentList) {
                boolean isNewDocument = true;
                if (uploadDocument.getId() != null) {
                    if (uploadDocument.getId() != 0) {
                        isNewDocument = false;
                    }
                }
                if (isNewDocument) {
                    documentService.addNewDocumentActivity(
                            firebaseUid,
                            oldActivity.getId(),
                            uploadDocument.getContentType(),
                            uploadDocument.getUri()
                    );
                } else {
                    documentService.addGroupDocumentToActivity(
                            firebaseUid,
                            oldActivity.getId(),
                            uploadDocument.getId()
                    );
                }
            }
            for (Document oldDocument : oldDocumentList) {
                boolean isNotContain = true;
                for (Document newDocument : uploadDocumentList) {
                    if (oldDocument.getId().equals(newDocument.getId())) {
                        isNotContain = false;
                    }
                }
                if (isNotContain) {
                    documentService.deleteDocumentActivity(firebaseUid, oldDocument.getId());
                }
            }
        }
        return oldActivity;
    }


    @Override
    public void deleteActivity(String firebaseUid, Integer idActivity) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Activity activity = getActivityById(idActivity);
        authenticationService.checkJoinedGroupByFirebaseUid(firebaseUid, activity.getPlan().getTrip().getGroup().getId());

        if (activity.getPlan().getIdStatus().equals(PlanStatus.ELECTED)) {
            authenticationService.checkTripOwner(person.getId(), activity.getPlan().getTrip().getId());
        } else {
            authenticationService.checkPlanOwner(person.getId(), activity.getPlan().getId());
        }
//        activity.setIdStatus(ActivityStatus.INACTIVE);
//        activityRepository.save(activity);

        activityRepository.deleteById(idActivity);
    }

    @Override
    public List<Activity> findAllActivityInPlan(String firebaseUid, Integer idPlan) {
        authenticationService.checkJoinedGroupByFirebaseUid(firebaseUid, planService.getPlanById(idPlan).getTrip().getGroup().getId());

        List<Activity> activityList = activityRepository.findAllByIdPlanAndIdActivityStatus(idPlan);
        for (Activity activity : activityList) {
            activity.setDocumentList(documentService.findAllDocumentActivity(firebaseUid, activity.getId()));
            activity.getTaskList().size();
            activity.getStartPlace().getPlaceImageList().size();
            activity.getEndPlace().getPlaceImageList().size();
        }
        return activityList;
    }

    @Override
    public Activity findActivityById(String firebaseUid, Integer idActivity) {
        Activity activity = activityRepository.findById(idActivity).get();
        activity.setDocumentList(documentService.findAllDocumentActivity(firebaseUid, activity.getId()));
        activity.getTaskList().size();
        activity.getStartPlace().getPlaceImageList().size();
        activity.getEndPlace().getPlaceImageList().size();
        return activity;
    }

    @Override
    public Activity getActivityById(Integer idActivity) {
        Optional<Activity> optionalActivity = activityRepository.findById(idActivity);
        if (optionalActivity.isPresent()) {
            return optionalActivity.get();
        } else {
            throw new NotFoundException("Can not found Activity");
        }
    }

    private void mapProperties(Activity source, Activity destination) {

        destination.setIsTooFar(source.getIsTooFar());

        Place startPlace = source.getStartPlace();
        Place endPlace = source.getEndPlace();

        //setup place
        if (startPlace != null) {
            destination.setStartPlace(
                    placeService.findOrCreatePlaceByGooglePlaceId(startPlace));
        }
        if (endPlace != null) {
            destination.setEndPlace(
                    placeService.findOrCreatePlaceByGooglePlaceId(endPlace));
        }
        destination.setStartAt(source.getStartAt());
        destination.setEndAt(source.getEndAt());
        destination.setName(source.getName());
        destination.setIdType(source.getIdType());

        destination.setStartUtcAt(
                ZonedDateTimeUtil.convertToUtcWithTimeZone(destination.getStartAt(), destination.getStartPlace().getTimeZone())
        );
        destination.setEndUtcAt(
                ZonedDateTimeUtil.convertToUtcWithTimeZone(destination.getEndAt(), destination.getEndPlace().getTimeZone())
        );

    }
}
