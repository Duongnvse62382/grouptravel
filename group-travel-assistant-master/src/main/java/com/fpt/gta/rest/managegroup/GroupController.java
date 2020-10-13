package com.fpt.gta.rest.managegroup;

import com.fpt.gta.model.entity.Group;
import com.fpt.gta.model.service.GroupService;
import com.fpt.gta.model.service.MemberService;
import com.google.firebase.auth.FirebaseToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {

    GroupService groupService;
    MemberService memberService;
    ModelMapper modelMapper;

    @Autowired
    public GroupController(GroupService groupService, MemberService memberService, ModelMapper modelMapper) {
        this.groupService = groupService;
        this.memberService = memberService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{idGroup}")
    public GroupDTO findGroupByIdGroup(@PathVariable String idGroup) {
        return modelMapper.map(groupService.getGroupById(Integer.parseInt(idGroup)), GroupDTO.class);
    }

    @GetMapping("/{idGroup}/preview")
    public GroupDTO previewGroup(@PathVariable String idGroup) {
        return modelMapper.map(groupService.getPreviewGroup(Integer.parseInt(idGroup)), GroupDTO.class);
    }

    @GetMapping
    public List<GroupDTO> findAllJoinedGroup(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken) {
        return Arrays.asList(
                modelMapper.map(groupService.findAllJoinedGroup(firebaseToken.getUid()), GroupDTO[].class));
    }

    @PostMapping
    public GroupDTO createGroup(@RequestBody GroupDTO groupDTO,
                                @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken) {
        Group group = groupService.createGroup(firebaseToken.getUid(), modelMapper.map(groupDTO, Group.class));
        return modelMapper.map(group, GroupDTO.class);
    }

    @PutMapping
    public void updateGroup(@RequestBody GroupDTO groupDTO,
                            @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken) {
        groupService.updateGroup(firebaseToken.getUid(), modelMapper.map(groupDTO, Group.class));
    }

    @DeleteMapping("/{idGroup}")
    public void leaveGroup(
            @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
            @PathVariable String idGroup) {
        groupService.leaveGroup(firebaseToken.getUid(), Integer.parseInt(idGroup));
    }

    @GetMapping("/{idGroup}/invitation")
    public String getInvitationCode(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken, @PathVariable String idGroup) {
        return groupService.getInvitationCode(firebaseToken.getUid(), Integer.parseInt(idGroup));
    }

    @PostMapping("/{idGroup}/invitation")
    public String getNewInvitationCode(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken, @PathVariable String idGroup) {
        return groupService.getNewInvitationCode(firebaseToken.getUid(), Integer.parseInt(idGroup));
    }

    @PostMapping("/{idGroup}/enroll")
    public GroupDTO enrollGroup(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                @PathVariable String idGroup,
                                @RequestParam String invitationCode) {
        Group group = groupService.enroll(firebaseToken.getUid(), invitationCode);
        return modelMapper.map(group, GroupDTO.class);
    }

    @GetMapping("/{idGroup}/members")
    public List<MemberDTO> findAllMemberInGroup(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                                @PathVariable String idGroup) {
        return Arrays.asList(modelMapper.map(memberService.findAllMemberInGroup(firebaseToken.getUid(), Integer.parseInt(idGroup)), MemberDTO[].class));
    }

    @PostMapping("/{idGroup}/makePending")
    public void makePending(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                            @PathVariable String idGroup
    ) {
        groupService.makePending(firebaseToken.getUid(), Integer.parseInt(idGroup));
    }

    @PostMapping("/{idGroup}/makePlanning")
    public void makePlanning(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                             @PathVariable String idGroup
    ) {
        groupService.makePlanning(firebaseToken.getUid(),
                Integer.parseInt(idGroup));
    }

    @PostMapping("/{idGroup}/makeMeReady")
    public void makeMemberReady(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                @PathVariable String idGroup,
                                @RequestParam boolean isReady
    ) {
        groupService.makeMemberReady(firebaseToken.getUid(), Integer.parseInt(idGroup), isReady);
    }

    @DeleteMapping("/{idGroup}/members/{idMember}")
    public void deActivateMemberInGroup(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                        @PathVariable String idGroup, @PathVariable String idMember) {
        groupService.deactivateMember(firebaseToken.getUid(), Integer.parseInt(idGroup), Integer.parseInt(idMember));
    }
}
