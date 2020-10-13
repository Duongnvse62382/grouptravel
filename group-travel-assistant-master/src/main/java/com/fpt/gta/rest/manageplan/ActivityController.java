package com.fpt.gta.rest.manageplan;

import com.fpt.gta.model.entity.Activity;
import com.fpt.gta.model.entity.Document;
import com.fpt.gta.model.service.ActivityService;
import com.fpt.gta.model.service.DocumentService;
import com.google.firebase.auth.FirebaseToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    ModelMapper modelMapper;
    ActivityService activityService;
    DocumentService documentService;

    @Autowired
    public ActivityController(ModelMapper modelMapper, ActivityService activityService, DocumentService documentService) {
        this.modelMapper = modelMapper;
        this.activityService = activityService;
        this.documentService = documentService;
    }

    @GetMapping
    public List<ActivityDTO> findAllActivityInPlan(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                                   @RequestParam Integer idPlan) {
        return Arrays.asList(
                modelMapper.map(
                        activityService.findAllActivityInPlan(firebaseToken.getUid(), idPlan),
                        ActivityDTO[].class
                )
        );
    }

    @GetMapping("/{idActivity}")
    public ActivityDTO findActivityById(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                                        @PathVariable String idActivity) {
        return modelMapper.map(activityService.findActivityById(firebaseToken.getUid(), Integer.parseInt(idActivity)), ActivityDTO.class);
    }


    @PostMapping
    public void createActivity(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                               @RequestParam Integer idPlan,
                               @RequestBody ActivityDTO activityDTO) {
        Activity activity = activityService.createActivityWithDocument(firebaseToken.getUid(),
                idPlan,
                modelMapper.map(activityDTO, Activity.class),
                activityDTO.getDocumentList() == null ?
                        null : Arrays.asList(modelMapper.map(activityDTO.getDocumentList(), Document[].class))
        );
    }

    @PutMapping
    public void updateActivity(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                               @RequestParam Integer idPlan,
                               @RequestBody ActivityDTO activityDTO) {
        activityService.updateActivityWithDocument(firebaseToken.getUid(),
                idPlan,
                modelMapper.map(activityDTO, Activity.class),
                activityDTO.getDocumentList() == null ?
                        null : Arrays.asList(modelMapper.map(activityDTO.getDocumentList(), Document[].class))
        );
    }

    @DeleteMapping("/{idActivity}")
    public void deleteActivity(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                               @PathVariable Integer idActivity) {
        activityService.deleteActivity(firebaseToken.getUid(), idActivity);
    }

}
