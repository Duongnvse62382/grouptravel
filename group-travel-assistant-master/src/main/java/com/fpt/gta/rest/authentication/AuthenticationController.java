package com.fpt.gta.rest.authentication;

import com.fpt.gta.model.service.PersonService;
import com.google.firebase.auth.FirebaseToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    PersonService personService;
    ModelMapper modelMapper;

    @Autowired
    public AuthenticationController(PersonService personService, ModelMapper modelMapper) {
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/sync")
    public PersonDTO syncFirebase(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken, @RequestBody(required = false) AppInstanceDTO appInstanceDTO) {

        return modelMapper.map(personService.syncFirebase(firebaseToken.getUid(), appInstanceDTO == null ? null : appInstanceDTO.getIdInstance()), PersonDTO.class);
    }

    @PostMapping("/signOut")
    public void signOut(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken, @RequestBody(required = false) AppInstanceDTO appInstanceDTO) {
        personService.signOut(firebaseToken.getUid(), appInstanceDTO == null ? null : appInstanceDTO.getIdInstance());
    }
}
