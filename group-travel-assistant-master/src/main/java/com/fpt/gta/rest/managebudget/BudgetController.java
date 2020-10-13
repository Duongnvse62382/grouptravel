package com.fpt.gta.rest.managebudget;

import com.fpt.gta.model.entity.Group;
import com.fpt.gta.model.entity.Member;
import com.fpt.gta.model.service.BudgetService;
import com.fpt.gta.rest.managegroup.GroupDTO;
import com.google.firebase.auth.FirebaseToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/budget")
public class BudgetController {

    private ModelMapper modelMapper;
    private BudgetService budgetService;

    @Autowired
    public BudgetController(ModelMapper modelMapper, BudgetService budgetService) {
        this.modelMapper = modelMapper;
        this.budgetService = budgetService;
    }

    @GetMapping("/electedPlanBudget")
    public List<TripDTO> getTripWithElectedPlan(
            @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
            @RequestParam String idGroup) {
        return Arrays.asList(
                modelMapper
                        .map(budgetService.getTripWithElectedPlan(firebaseToken.getUid(), Integer.parseInt(idGroup)),
                                TripDTO[].class));
    }

    @GetMapping("/myBudget")
    public MemberDTO findMyBudget(
            @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
            @RequestParam String idGroup) {
        return modelMapper.map(budgetService.getMyBudgetInGroup(firebaseToken.getUid(), Integer.parseInt(idGroup)),
                MemberDTO.class);
    }

    @PostMapping("/myBudget")
    public void updateMyBudget(
            @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
            @RequestParam String idGroup,
            @RequestBody(required = false) MemberDTO memberDTO
    ) {
        budgetService.updateMyBudgetInGroup(
                firebaseToken.getUid(),
                Integer.parseInt(idGroup),
                modelMapper.map(memberDTO, Member.class));
    }

    @PostMapping("/groupBudget")
    public void updateGroupBudget(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                  @RequestParam String idGroup,
                                  @RequestBody(required = false) GroupDTO groupDTO
    ) {
        budgetService.updateGroupBudget(firebaseToken.getUid(), Integer.parseInt(idGroup), modelMapper.map(groupDTO, Group.class));
    }
}
