package com.fpt.gta.scheduler;

import com.fpt.gta.model.constant.FirebaseDatabaseConstant;
import com.fpt.gta.model.constant.PlanStatus;
import com.fpt.gta.model.entity.Plan;
import com.fpt.gta.model.entity.Trip;
import com.fpt.gta.model.repository.PlanRepository;
import com.fpt.gta.model.repository.TripRepository;
import com.fpt.gta.model.service.PlanService;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class PlanVotingScheduler {

    PlanRepository planRepository;
    PlanService planService;
    TripRepository tripRepository;

    @Autowired
    public PlanVotingScheduler(PlanRepository planRepository, PlanService planService, TripRepository tripRepository) {
        this.planRepository = planRepository;
        this.planService = planService;
        this.tripRepository = tripRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    public void fixedDelaySch() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = new Date();
        now = ZonedDateTimeUtil.convertToUtcWithTimeZone(new Timestamp(now.getTime()), ZoneId.systemDefault().toString());
        now = simpleDateFormat.parse(simpleDateFormat.format(now));

        Date after = new Date(now.getTime() + 60 * 1000);
        List<Trip> tripList = tripRepository.findAllTripByVoteEndAtBetween(now, after);
        for (Trip trip : tripList) {
            List<Plan> electedPlanList = planRepository.findAllPlanByIdTripAndIdStatus(trip.getId(), PlanStatus.ELECTED);
            if (electedPlanList.size() == 0) {
                List<Plan> highestPlanVotingList = planService.getHighestVotingPlanListWithoutPermission(trip.getId());
                if (highestPlanVotingList.size() == 1) {
                    planService.pickHighestVotePlanWithoutPermission(highestPlanVotingList.get(0).getId());
                }
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference(FirebaseDatabaseConstant.getReloadPlanPath(trip.getGroup().getId()));
                ref.setValueAsync(Instant.now().toEpochMilli());
            }
        }

    }
}
