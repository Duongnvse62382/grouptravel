package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    Member getMemberByIdPersonAndIdGroup(Integer idPerson, Integer idGroup);

    Member getMemberByIdMember(Integer idMember);

    List<Member> findAllMemberInGroup(String firebaseUid, Integer idGroup);

    Member saveMember(Member member);

    Optional<Member> findActiveMemberByIdPersonAndIdGroup(Integer idPerson, Integer idGroup);

    Member getByIdPersonAndIdGroup(Integer idPerson, Integer idGroup);

    Integer countMemberByIdGroupAndIdStatus(Integer idGroup, Integer idStatus);

    List<Member> findAllByIdPerson(Integer idPerson);
}
