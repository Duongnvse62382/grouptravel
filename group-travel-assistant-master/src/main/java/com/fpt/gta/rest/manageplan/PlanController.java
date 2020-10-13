package com.fpt.gta.rest.manageplan;

import com.fpt.gta.model.entity.Activity;
import com.fpt.gta.model.entity.Plan;
import com.fpt.gta.model.service.PlanService;
import com.google.firebase.auth.FirebaseToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/plans")
public class PlanController {

    ModelMapper modelMapper;
    PlanService planService;

    @Autowired
    public PlanController(ModelMapper modelMapper, PlanService planService) {
        this.modelMapper = modelMapper;
        this.planService = planService;
    }

    @GetMapping
    public List<PlanDTO> findAllPlanInTrip(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                           @RequestParam Integer idTrip) {
        List<PlanDTO> planDTOList = Arrays.asList(modelMapper.map(planService.findAllPublicPlanInTrip(firebaseToken.getUid(), idTrip), PlanDTO[].class));
        return planDTOList;
    }

    @PostMapping
    public PlanDTO createPlan(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                              @RequestParam Integer idTrip,
                              @RequestBody(required = false) List<ActivityDTO> activityList
    ) {
        List<Activity> list = Arrays.asList(modelMapper.map(activityList, Activity[].class));
        return modelMapper.map(planService.createPlan(firebaseToken.getUid(), idTrip, list), PlanDTO.class);
    }

    @DeleteMapping("/{idPlan}")
    public void deletePlan(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken, @PathVariable String idPlan) {
        planService.removePlan(firebaseToken.getUid(), Integer.parseInt(idPlan));
    }

    @PutMapping("/{idPlan}")
    public void setPlanBudget(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                              @PathVariable String idPlan,
                              @RequestParam BigDecimal activityBudget,
                              @RequestParam BigDecimal accommodationBudget,
                              @RequestParam BigDecimal transportationBudget,
                              @RequestParam BigDecimal foodBudget
    ) {
        planService.setPlanBudget(firebaseToken.getUid(),
                Integer.parseInt(idPlan),
                activityBudget,
                accommodationBudget,
                transportationBudget,
                foodBudget);
    }

    @PostMapping("/publish")
    public void publishPlan(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                            @PathVariable String idPlan) {
        planService.publishPlan(firebaseToken.getUid(), Integer.parseInt(idPlan));
    }

    @PostMapping("/conceal")
    public void concealPlan(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                            @PathVariable String idPlan) {
        planService.concealPlan(firebaseToken.getUid(), Integer.parseInt(idPlan));
    }

    @PostMapping("/{idPlan}/vote")
    public void votePlan(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                         @PathVariable String idPlan) {
        planService.votePlan(firebaseToken.getUid(), Integer.parseInt(idPlan));
    }

    @DeleteMapping("/{idPlan}/vote")
    public void removeVotePlan(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                               @PathVariable String idPlan) {
        planService.removeVotePlan(firebaseToken.getUid(), Integer.parseInt(idPlan));
    }

    @GetMapping("/highestVotePlan")
    public List<PlanDTO> getHighestVotePlan(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                            @RequestParam String idTrip) {
        return Arrays.asList(
                modelMapper.map(planService.getHighestVotingPlanList(firebaseToken.getUid(), Integer.parseInt(idTrip)),
                        PlanDTO[].class
                ));
    }

    @PostMapping("/pickHighestVotePlan")
    public void pickHighestVotePlan(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                    @RequestParam String idPlan) {
        planService.pickHighestVotePlan(firebaseToken.getUid(), Integer.parseInt(idPlan));
    }

    @PatchMapping("/changeVoteDeadline")
    public void changeVoteDeadline(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                   @RequestBody TripDTO tripDTO
    ) {
        planService.changeVoteDeadline(firebaseToken.getUid(), tripDTO.getId(), tripDTO.getVoteEndAt());
    }
}
