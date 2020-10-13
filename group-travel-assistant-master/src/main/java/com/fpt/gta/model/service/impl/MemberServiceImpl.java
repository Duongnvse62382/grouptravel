package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.NotFoundException;
import com.fpt.gta.model.constant.MemberStatus;
import com.fpt.gta.model.entity.Group;
import com.fpt.gta.model.entity.Member;
import com.fpt.gta.model.entity.Person;
import com.fpt.gta.model.repository.MemberRepository;
import com.fpt.gta.model.service.AuthenticationService;
import com.fpt.gta.model.service.MemberService;
import com.fpt.gta.model.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    MemberRepository memberRepository;
    AuthenticationService authenticationService;
    PersonService personService;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, AuthenticationService authenticationService, PersonService personService) {
        this.memberRepository = memberRepository;
        this.authenticationService = authenticationService;
        this.personService = personService;
    }

    @Override
    public Member getMemberByIdMember(Integer idMember) {
        Optional<Member> optionalMember = memberRepository.findById(idMember);
        if (optionalMember.isPresent()) {
            return optionalMember.get();
        } else {
            throw new NotFoundException("Not found Member");
        }
    }

    @Override
    public Member getMemberByIdPersonAndIdGroup(Integer idPerson, Integer idGroup) {
        Optional<Member> optionalMember = memberRepository.findActiveMemberByIdPersonAndIdGroup(idPerson, idGroup);
        if (optionalMember.isPresent()) {
            return optionalMember.get();
        } else {
            throw new NotFoundException("Not found Member");
        }
    }


    public List<Member> findAllMemberInGroup(String firebaseUid, Integer idGroup) {
        authenticationService.checkJoinedGroupByFirebaseUid(firebaseUid, idGroup);
        return memberRepository.findAllByIdGroup(idGroup);
    }

    @Override
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public Optional<Member> findActiveMemberByIdPersonAndIdGroup(Integer idPerson, Integer idGroup) {
        return memberRepository.findActiveMemberByIdPersonAndIdGroup(idPerson, idGroup);
    }

    @Override
    public Member getByIdPersonAndIdGroup(Integer idPerson, Integer idGroup) {
        Optional<Member> optionalMember = memberRepository.findActiveMemberByIdPersonAndIdGroup(idPerson, idGroup);
        if (optionalMember.isPresent()) {
            return optionalMember.get();
        } else {
            throw new NotFoundException("Not Found Member");
        }
    }

    @Override
    public Integer countMemberByIdGroupAndIdStatus(Integer idGroup, Integer idStatus) {
        return memberRepository.countMemberByIdGroupAndIdStatus(idGroup, idStatus);
    }

    @Override
    public List<Member> findAllByIdPerson(Integer idPerson) {
        return memberRepository.findAllByIdPerson(idPerson);
    }
}
