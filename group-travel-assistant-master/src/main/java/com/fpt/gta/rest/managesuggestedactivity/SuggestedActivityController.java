package com.fpt.gta.rest.managesuggestedactivity;

import com.fpt.gta.model.entity.SuggestedActivity;
import com.fpt.gta.model.service.SuggestedActivityService;
import com.google.firebase.auth.FirebaseToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/suggested-activities")
public class SuggestedActivityController {

    ModelMapper modelMapper;
    SuggestedActivityService suggestedActivityService;

    @Autowired
    public SuggestedActivityController(ModelMapper modelMapper,
                                       SuggestedActivityService suggestedActivityService) {
        this.modelMapper = modelMapper;
        this.suggestedActivityService = suggestedActivityService;
    }

    @GetMapping("/{idSuggestedActivity}")
    public SuggestedActivityDTO findSuggestedActivityByIdSuggestedActivity
            (@PathVariable String idSuggestedActivity) {
        return modelMapper
                .map(suggestedActivityService
                                .getSuggestedActivityById(Integer.parseInt(idSuggestedActivity)),
                        SuggestedActivityDTO.class);
    }

    @GetMapping
    public List<SuggestedActivityDTO> findAllJoinedGroup(@RequestAttribute("FirebaseToken")
                                                                 FirebaseToken firebaseToken,
                                                         @RequestParam("idTrip") String idTrip) {

        List<SuggestedActivityDTO> suggestedActivityDTOList = Arrays.asList(
                modelMapper.map(suggestedActivityService
                                .findAllSuggestedActivityInTrip(Integer.parseInt(idTrip)),
                        SuggestedActivityDTO[].class));
        return suggestedActivityDTOList;
    }

    @PostMapping
    public void createSuggestedActivity(@RequestBody SuggestedActivityDTO suggestedActivityDTO,
                                        @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                        @RequestParam("idTrip") String idTrip
    ) {
        suggestedActivityService.createSuggestedActivity(firebaseToken.getUid(), Integer.parseInt(idTrip), modelMapper.map(suggestedActivityDTO, SuggestedActivity.class));
    }

    @PutMapping
    public void updateSuggestedActivity(@RequestBody SuggestedActivityDTO suggestedActivityDTO,
                                        @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken
    ) {
        suggestedActivityService.updateSuggestedActivity(firebaseToken.getUid(), modelMapper.map(suggestedActivityDTO, SuggestedActivity.class));
    }

    @DeleteMapping("/{idSuggestedActivity}")
    public void deleteSuggestedActivity(
            @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
            @PathVariable String idSuggestedActivity) {
        suggestedActivityService.removeSuggestedActivity(firebaseToken.getUid(), Integer.parseInt(idSuggestedActivity));
    }

    @PostMapping("/{idSuggestedActivity}/vote")
    public void voteSuggestedActivity(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                      @PathVariable String idSuggestedActivity) {
        suggestedActivityService.voteSuggestedActivity(firebaseToken.getUid(), Integer.parseInt(idSuggestedActivity));
    }

    @DeleteMapping("/{idSuggestedActivity}/vote")
    public void removeVoteSuggestedActivity(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                            @PathVariable String idSuggestedActivity) {
        suggestedActivityService.removeVoteSuggestedActivity(firebaseToken.getUid(), Integer.parseInt(idSuggestedActivity));
    }


}
