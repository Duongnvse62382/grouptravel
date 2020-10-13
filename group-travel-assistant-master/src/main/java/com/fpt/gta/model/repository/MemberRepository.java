package com.fpt.gta.model.repository;

import com.fpt.gta.model.constant.MemberStatus;
import com.fpt.gta.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    @Query("Select m from Member m where m.person.id =?1")
    List<Member> findAllByIdPerson(Integer idPerson);

    @Query("Select m from Member m where m.joinedGroup.id =?1")
    List<Member> findAllByIdGroup(Integer idGroup);

    @Query("Select m from Member m where m.joinedGroup.id =?1 and m.idRole=?2")
    List<Member> findAllByIdGroupAndIdRole(Integer idGroup, Integer idRole);

    @Query("Select m from Member m where  m.person.id=?1  and m.joinedGroup.id =?2 and m.idStatus=" + MemberStatus.ACTIVE)
    Optional<Member> findActiveMemberByIdPersonAndIdGroup(Integer idPerson, Integer idGroup);

    @Query("Select count(m) from Member m where m.joinedGroup.id=?1 and m.idStatus=?2")
    Integer countMemberByIdGroupAndIdStatus(Integer idGroup, Integer idStatus);

    @Query("Select m from Member m where m.joinedGroup.id =?1 and m.idStatus=?2")
    List<Member> findAllByIdJoinedGroupAndIdStatus(Integer idGroup, Integer idStatus);

}
