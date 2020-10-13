package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Group;
import com.fpt.gta.model.entity.Member;
import com.fpt.gta.model.entity.Trip;

import java.util.List;

public interface BudgetService {
    List<Trip> getTripWithElectedPlan(String firebaseUid, Integer idGroup);

    Member getMyBudgetInGroup(String firebaseUid, Integer idGroup);

    void updateMyBudgetInGroup(String firebaseUid, Integer idGroup, Member member);

    void updateGroupBudget(String firebaseUid, Integer idGroup, Group group);
}
