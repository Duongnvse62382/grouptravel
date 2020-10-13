package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Group;

import java.util.List;

public interface GroupService {

    Group getGroupById(Integer groupId);

    List<Group> findAllJoinedGroup(String firebaseUid);

    Group createGroup(String firebaseUid, Group group);

    void updateGroup(String firebaseUid, Group group);

    void leaveGroup(String firebaseUid, Integer idGroup);

    Group getPreviewGroup(Integer idGroup);

    String getInvitationCode(String firebaseUid, Integer idGroup);

    String getNewInvitationCode(String firebaseUid, Integer idGroup);

    Group enroll(String firebaseUid, String invitationCode);

    void deactivateMember(String firebaseUid, Integer idGroup, Integer idMember);

    void makePending(String firebaseUid, Integer idGroup);

    void makePlanning(String firebaseUid, Integer idGroup);

    void makeMemberReady(String firebaseUid, Integer idGroup, Boolean isReady);
}
