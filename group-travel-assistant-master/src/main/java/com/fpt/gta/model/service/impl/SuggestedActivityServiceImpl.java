package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.ForbiddenException;
import com.fpt.gta.exception.NotFoundException;
import com.fpt.gta.model.constant.MemberStatus;
import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.repository.SuggestedActivityRepository;
import com.fpt.gta.model.repository.VotedSuggestedActivityRepository;
import com.fpt.gta.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SuggestedActivityServiceImpl implements SuggestedActivityService {

    SuggestedActivityRepository suggestedActivityRepository;
    VotedSuggestedActivityRepository votedSuggestedActivityRepository;
    TripService tripService;
    PlaceService placeService;
    PersonService personService;
    MemberService memberService;
    AuthenticationService authenticationService;

    @Autowired
    public SuggestedActivityServiceImpl(SuggestedActivityRepository suggestedActivityRepository, VotedSuggestedActivityRepository votedSuggestedActivityRepository, TripService tripService, PlaceService placeService, PersonService personService, MemberService memberService, AuthenticationService authenticationService) {
        this.suggestedActivityRepository = suggestedActivityRepository;
        this.votedSuggestedActivityRepository = votedSuggestedActivityRepository;
        this.tripService = tripService;
        this.placeService = placeService;
        this.personService = personService;
        this.memberService = memberService;
        this.authenticationService = authenticationService;
    }

    @Override
    public SuggestedActivity getSuggestedActivityById(Integer idSuggestedActivity) {
        Optional<SuggestedActivity> optionalSuggestedActivity = suggestedActivityRepository.findById(idSuggestedActivity);
        if (optionalSuggestedActivity.isPresent()) {
            SuggestedActivity suggestedActivity = optionalSuggestedActivity.get();
            suggestedActivity.getStartPlace().getPlaceImageList().size();
            suggestedActivity.getEndPlace().getPlaceImageList().size();
            return suggestedActivity;
        } else {
            throw new NotFoundException("Not Found Suggested Activity");
        }
    }


    @Override
    public List<SuggestedActivity> findAllSuggestedActivityInTrip(Integer idTrip) {
        List<SuggestedActivity> suggestedActivityList = suggestedActivityRepository.findAllByIdTrip(idTrip);
        for (SuggestedActivity sa : suggestedActivityList) {

            sa.getVotedSuggestedActivityList().size();
            for (int i = sa.getVotedSuggestedActivityList().size() - 1; i >= 0; i--) {
                if (sa.getVotedSuggestedActivityList().get(i).getMember().getIdStatus().equals(MemberStatus.INACTIVE)) {
                    sa.getVotedSuggestedActivityList().remove(i);
                }
            }

            sa.getStartPlace().getPlaceImageList().size();
            sa.getEndPlace().getPlaceImageList().size();
        }
        return suggestedActivityList;
    }

    @Override
    public List<SuggestedActivity> findAllSuggestedActivityInTripByType(Integer idTrip, Integer idType) {
        List<SuggestedActivity> suggestedActivityList = suggestedActivityRepository.findActivityIdTrip(idTrip, idType);
        for (SuggestedActivity sa : suggestedActivityList) {
            sa.getVotedSuggestedActivityList();
            sa.getStartPlace().getPlaceImageList().size();
            sa.getEndPlace().getPlaceImageList().size();
        }
        return suggestedActivityList;
    }

    @Override
    public void createSuggestedActivity(String firebaseUid, Integer idTrip, SuggestedActivity suggestedActivity) {
        Trip trip = tripService.getTripById(idTrip);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Member owner = memberService.getMemberByIdPersonAndIdGroup(person.getId(), trip.getGroup().getId());
        authenticationService.checkJoinedGroup(person.getId(), trip.getGroup().getId());
        SuggestedActivity newSuggestedActivity = new SuggestedActivity();
        mapProperty(suggestedActivity, newSuggestedActivity);
        //prepare relationship and other field
        newSuggestedActivity.setTrip(trip);
        newSuggestedActivity.setOwner(owner);
        suggestedActivityRepository.save(newSuggestedActivity);

    }

    @Override
    public void updateSuggestedActivity(String firebaseUid, SuggestedActivity suggestedActivity) {
        SuggestedActivity oldSuggestedActivity = getSuggestedActivityById(suggestedActivity.getId());
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        authenticationService.checkSuggestedActivityOwner(person.getId(), suggestedActivity.getId());

        mapProperty(suggestedActivity, oldSuggestedActivity);
        suggestedActivityRepository.save(oldSuggestedActivity);
    }

    @Override
    public void removeSuggestedActivity(String firebaseUid, Integer idSuggestedActivity) {
        SuggestedActivity suggestedActivity = getSuggestedActivityById(idSuggestedActivity);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);

        if (authenticationService.isSuggestedActivityOwner(person.getId(), idSuggestedActivity)
                || authenticationService.isGroupOwner(person.getId(), suggestedActivity.getTrip().getGroup().getId())
        ) {
            suggestedActivityRepository.deleteById(idSuggestedActivity);
        } else {
            throw new ForbiddenException("You cant not delete this suggested activity with your permission");
        }
    }

    @Override
    public void voteSuggestedActivity(String firebaseUid, Integer idSuggestedActivity) {
        SuggestedActivity oldSuggestedActivity = getSuggestedActivityById(idSuggestedActivity);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Member member = memberService.getMemberByIdPersonAndIdGroup(person.getId(),
                oldSuggestedActivity.getTrip().getGroup().getId());
        VotedSuggestedActivity votedSuggestedActivity = new VotedSuggestedActivity();
        votedSuggestedActivity.setMember(member);
        votedSuggestedActivity.setSuggestedActivity(oldSuggestedActivity);
        votedSuggestedActivityRepository.save(votedSuggestedActivity);
    }

    @Override
    public void removeVoteSuggestedActivity(String firebaseUid, Integer idSuggestedActivity) {
        SuggestedActivity oldSuggestedActivity = getSuggestedActivityById(idSuggestedActivity);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Member member = memberService.getMemberByIdPersonAndIdGroup(person.getId(),
                oldSuggestedActivity.getTrip().getGroup().getId());
        VotedSuggestedActivity votedSuggestedActivity
                = votedSuggestedActivityRepository.findByIdMemberAndIdSuggestedActivity(member.getId(), idSuggestedActivity);

        votedSuggestedActivityRepository.deleteById(votedSuggestedActivity.getId());
    }

    private void mapProperty(SuggestedActivity suggestedActivity, SuggestedActivity destination) {

        destination.setIsTooFar(suggestedActivity.getIsTooFar());

        Place startPlace = suggestedActivity.getStartPlace();
        Place endPlace = suggestedActivity.getEndPlace();

        //setup place
        if (startPlace != null) {
            destination.setStartPlace(
                    placeService.findOrCreatePlaceByGooglePlaceId(startPlace));
        } else {
            destination.setStartPlace(null);
        }
        if (endPlace != null) {
            destination.setEndPlace(
                    placeService.findOrCreatePlaceByGooglePlaceId(endPlace));
        } else {
            destination.setEndPlace(null);
        }
        destination.setName(suggestedActivity.getName());
        destination.setIdType(suggestedActivity.getIdType());
    }
}
