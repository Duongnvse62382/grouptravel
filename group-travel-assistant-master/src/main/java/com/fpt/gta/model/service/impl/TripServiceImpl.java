package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.NotFoundException;
import com.fpt.gta.model.constant.PlanStatus;
import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.repository.GroupRepository;
import com.fpt.gta.model.repository.PlanRepository;
import com.fpt.gta.model.repository.SuggestedActivityRepository;
import com.fpt.gta.model.repository.TripRepository;
import com.fpt.gta.model.service.*;
import com.fpt.gta.util.ZonedDateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TripServiceImpl implements TripService {

    TripRepository tripRepository;
    PlaceService placeService;
    PersonService personService;
    AuthenticationService authenticationService;
    PlanRepository planRepository;
    GroupService groupService;
    GroupRepository groupRepository;
    SuggestedActivityRepository suggestedActivityRepository;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository, PlaceService placeService, PersonService personService, AuthenticationService authenticationService, PlanRepository planRepository, GroupService groupService, GroupRepository groupRepository, SuggestedActivityRepository suggestedActivityRepository) {
        this.tripRepository = tripRepository;
        this.placeService = placeService;
        this.personService = personService;
        this.authenticationService = authenticationService;
        this.planRepository = planRepository;
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.suggestedActivityRepository = suggestedActivityRepository;
    }

    @Override
    public Trip getTripById(Integer idTrip) {
        Optional<Trip> optionalTrip = tripRepository.findById(idTrip);
        if (optionalTrip.isPresent()) {
            return optionalTrip.get();
        } else {
            throw new NotFoundException("Can not found Trip");
        }
    }

    @Override
    public Trip getTripById2(Integer idTrip) {
        Optional<Trip> optionalTrip = tripRepository.findById(idTrip);
        if (optionalTrip.isPresent()) {
            return optionalTrip.get();
        }
        return null;
    }

    @Override
    public List<Trip> findAllTripInGroup(Integer idGroup) {
        List<Trip> tripList = tripRepository.findTripsByIdGroup(idGroup);
        for (Trip trip : tripList) {
            trip.getEndPlace().getPlaceImageList().size();
            for (Plan plan : trip.getPlanList()) {
                if (plan.getIdStatus().equals(PlanStatus.ELECTED)) {
                    trip.setElectedPlan(plan);
                    break;
                }
            }
        }
        return tripList;
    }

    @Override
    public void createTrip(String firebaseUid, Integer idGroup, Trip trip) {
        Person newPerson = personService.getPersonByFirebaseUid(firebaseUid);
        Group group = groupService.getGroupById(idGroup);
        Place place = placeService.findOrCreatePlaceByGooglePlaceId(trip.getEndPlace());

        //        setup Trip before create
        trip.setEndPlace(place);
        trip.setStartAt(trip.getStartAt());
        trip.setEndAt(trip.getEndAt());
        trip.setGroup(group);
        trip.setStartUtcAt(
                ZonedDateTimeUtil.convertToUtcWithTimeZone(trip.getStartAt(), trip.getEndPlace().getTimeZone())
        );
        trip.setEndUtcAt(
                ZonedDateTimeUtil.convertToUtcWithTimeZone(trip.getEndAt(), trip.getEndPlace().getTimeZone())
        );

        //update group time
//        LocalDateTime earliestUtcLocalDT = null;
//        LocalDateTime latestUtcLocalDT = null;
//
//        for (Trip eachTrip : group.getTripList()) {
//            LocalDateTime eachTripStartUtc = eachTrip.getStartUtcAt().toLocalDateTime();
//            LocalDateTime eachTripEndUtc = eachTrip.getEndUtcAt().toLocalDateTime();
//            if (earliestUtcLocalDT == null) {
//                earliestUtcLocalDT = eachTripStartUtc;
//            }
//            if (latestUtcLocalDT == null) {
//                latestUtcLocalDT = eachTripEndUtc;
//            }
//            if (eachTripStartUtc.isBefore(earliestUtcLocalDT)) {
//                earliestUtcLocalDT = eachTripStartUtc;
//            }
//            if (eachTripEndUtc.isAfter(latestUtcLocalDT)) {
//                latestUtcLocalDT = eachTripEndUtc;
//            }
//        }
//
//        LocalDateTime updatedTripStartUtc = trip.getStartUtcAt().toLocalDateTime();
//        LocalDateTime updatedTripEndUtc = trip.getEndUtcAt().toLocalDateTime();
//        if (earliestUtcLocalDT == null) {
//            earliestUtcLocalDT = updatedTripStartUtc;
//        }
//        if (latestUtcLocalDT == null) {
//            latestUtcLocalDT = updatedTripEndUtc;
//        }
//        if (updatedTripStartUtc.isBefore(earliestUtcLocalDT)) {
//            earliestUtcLocalDT = updatedTripStartUtc;
//        }
//        if (updatedTripEndUtc.isAfter(latestUtcLocalDT)) {
//            latestUtcLocalDT = updatedTripEndUtc;
//        }
//
//        if (earliestUtcLocalDT != null && latestUtcLocalDT != null) {
//            group.setStartUtcAt(Timestamp.valueOf(earliestUtcLocalDT));
//            group.setEndUtcAt(Timestamp.valueOf(latestUtcLocalDT));
//            group.setStartAt(Timestamp.valueOf(
//                    ZonedDateTimeUtil.convertFromUtcToTimeZone(
//                            group.getStartUtcAt(), group.getStartPlace().getTimeZone()
//                    ).toLocalDateTime().toLocalDate().atStartOfDay())
//            );
//            group.setEndAt(Timestamp.valueOf(
//                    ZonedDateTimeUtil.convertFromUtcToTimeZone(
//                            group.getEndUtcAt(), group.getStartPlace().getTimeZone()
//                    ).toLocalDateTime().toLocalDate().atTime(23,59,59))
//            );
//            group = groupRepository.save(group);
//        }
// END update group time

        //      create Trip
        Trip newTrip = tripRepository.save(trip);

    }

    @Override
    public void updateTrip(String firebaseUid, Trip trip) {
//        prepare Trip entity
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Place place = placeService.findOrCreatePlaceByGooglePlaceId(trip.getEndPlace());
        Trip oldTrip = getTripById(trip.getId());
        Group group = groupRepository.findById(oldTrip.getGroup().getId()).get();

        //        check owner
        authenticationService.checkGroupOwner(person.getId(), group.getId());
        boolean isPlaceChange = !oldTrip.getEndPlace().getId().equals(place.getId());
        if (isPlaceChange) {
            for (Plan plan : oldTrip.getPlanList()) {
                planRepository.deleteById(plan.getId());
            }
            for (SuggestedActivity suggestedActivity : oldTrip.getSuggestedActivityList()) {
                suggestedActivityRepository.deleteById(suggestedActivity.getId());
            }
        }

        //        put update data
        oldTrip.setEndPlace(place);
        oldTrip.setStartAt(trip.getStartAt());
        oldTrip.setEndAt(trip.getEndAt());

        oldTrip.setStartUtcAt(
                ZonedDateTimeUtil.convertToUtcWithTimeZone(oldTrip.getStartAt(),
                        oldTrip.getEndPlace().getTimeZone())
        );

        oldTrip.setEndUtcAt(
                ZonedDateTimeUtil.convertToUtcWithTimeZone(oldTrip.getEndAt(),
                        oldTrip.getEndPlace().getTimeZone())
        );

        //update group time
//        LocalDateTime earliestUtcLocalDT = null;
//        LocalDateTime latestUtcLocalDT = null;
//
//        for (Trip eachTrip : group.getTripList()) {
//            if (!oldTrip.getId().equals(eachTrip)) {
//                LocalDateTime eachTripStartUtc = eachTrip.getStartUtcAt().toLocalDateTime();
//                LocalDateTime eachTripEndUtc = eachTrip.getEndUtcAt().toLocalDateTime();
//                if (earliestUtcLocalDT == null) {
//                    earliestUtcLocalDT = eachTripStartUtc;
//                }
//                if (latestUtcLocalDT == null) {
//                    latestUtcLocalDT = eachTripEndUtc;
//                }
//                if (eachTripStartUtc.isBefore(earliestUtcLocalDT)) {
//                    earliestUtcLocalDT = eachTripStartUtc;
//                }
//                if (eachTripEndUtc.isAfter(latestUtcLocalDT)) {
//                    latestUtcLocalDT = eachTripEndUtc;
//                }
//            }
//        }
//
//        LocalDateTime updatedTripStartUtc = oldTrip.getStartUtcAt().toLocalDateTime();
//        LocalDateTime updatedTripEndUtc = oldTrip.getEndUtcAt().toLocalDateTime();
//        if (earliestUtcLocalDT == null) {
//            earliestUtcLocalDT = updatedTripStartUtc;
//        }
//        if (latestUtcLocalDT == null) {
//            latestUtcLocalDT = updatedTripEndUtc;
//        }
//        if (updatedTripStartUtc.isBefore(earliestUtcLocalDT)) {
//            earliestUtcLocalDT = updatedTripStartUtc;
//        }
//        if (updatedTripEndUtc.isAfter(latestUtcLocalDT)) {
//            latestUtcLocalDT = updatedTripEndUtc;
//        }
//
//        if (earliestUtcLocalDT != null && latestUtcLocalDT != null) {
//            group.setStartUtcAt(Timestamp.valueOf(earliestUtcLocalDT));
//            group.setEndUtcAt(Timestamp.valueOf(latestUtcLocalDT));
//            group.setStartAt(Timestamp.valueOf(
//                    ZonedDateTimeUtil.convertFromUtcToTimeZone(
//                            group.getStartUtcAt(), group.getStartPlace().getTimeZone()
//                    ).toLocalDateTime().toLocalDate().atStartOfDay())
//            );
//            group.setEndAt(Timestamp.valueOf(
//                    ZonedDateTimeUtil.convertFromUtcToTimeZone(
//                            group.getEndUtcAt(), group.getStartPlace().getTimeZone()
//                    ).toLocalDateTime().toLocalDate().atTime(23,59,59))
//            );
//            group = groupRepository.save(group);
//        }
// END update group time

        //        update trip
        tripRepository.save(oldTrip);

    }

    @Override
    public void deleteTrip(String firebaseUid, Integer idTrip) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        authenticationService.checkTripOwner(person.getId(), idTrip);

        Trip trip = getTripById(idTrip);
        Group group = trip.getGroup();
        //update group time
//        LocalDateTime earliestUtcLocalDT = null;
//        LocalDateTime latestUtcLocalDT = null;
//
//        for (Trip eachTrip : group.getTripList()) {
//            if (!eachTrip.getId().equals(idTrip)) {
//                LocalDateTime eachTripStartUtc = eachTrip.getStartUtcAt().toLocalDateTime();
//                LocalDateTime eachTripEndUtc = eachTrip.getEndUtcAt().toLocalDateTime();
//                if (earliestUtcLocalDT == null) {
//                    earliestUtcLocalDT = eachTripStartUtc;
//                }
//                if (latestUtcLocalDT == null) {
//                    latestUtcLocalDT = eachTripEndUtc;
//                }
//                if (eachTripStartUtc.isBefore(earliestUtcLocalDT)) {
//                    earliestUtcLocalDT = eachTripStartUtc;
//                }
//                if (eachTripEndUtc.isAfter(latestUtcLocalDT)) {
//                    latestUtcLocalDT = eachTripEndUtc;
//                }
//            }
//        }
//        if (earliestUtcLocalDT != null && latestUtcLocalDT != null) {
//            group.setStartUtcAt(Timestamp.valueOf(earliestUtcLocalDT));
//            group.setEndUtcAt(Timestamp.valueOf(latestUtcLocalDT));
//            group.setStartAt(Timestamp.valueOf(
//                    ZonedDateTimeUtil.convertFromUtcToTimeZone(
//                            group.getStartUtcAt(), group.getStartPlace().getTimeZone()
//                    ).toLocalDateTime().toLocalDate().atStartOfDay())
//            );
//            group.setEndAt(Timestamp.valueOf(
//                    ZonedDateTimeUtil.convertFromUtcToTimeZone(
//                            group.getEndUtcAt(), group.getStartPlace().getTimeZone()
//                    ).toLocalDateTime().toLocalDate().atTime(23,59,59))
//            );
//            group = groupRepository.save(group);
//        }
// END update group time
        tripRepository.deleteById(idTrip);
    }

}
