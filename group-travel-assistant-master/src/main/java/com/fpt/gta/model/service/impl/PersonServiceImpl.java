package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.ForbiddenException;
import com.fpt.gta.exception.InternalServerErrorException;
import com.fpt.gta.exception.NotFoundException;
import com.fpt.gta.model.constant.PersonStatus;
import com.fpt.gta.model.entity.AppInstance;
import com.fpt.gta.model.entity.Person;
import com.fpt.gta.model.repository.AppInstanceRepository;
import com.fpt.gta.model.repository.PersonRepository;
import com.fpt.gta.model.service.PersonService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    PersonRepository personRepository;
    AppInstanceRepository appInstanceRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository, AppInstanceRepository appInstanceRepository) {
        this.personRepository = personRepository;
        this.appInstanceRepository = appInstanceRepository;
    }

    @Override
    public Person syncFirebase(String firebaseUid, String idInstance) {
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            UserRecord userRecord = firebaseAuth.getUser(firebaseUid);
            Optional<Person> optionalPerson = personRepository.findByFirebaseUid(firebaseUid);

            Person updatedPerson = null;

            if (optionalPerson.isPresent()) {
                updatedPerson = optionalPerson.get();
            } else {
                updatedPerson = new Person();
            }

            if (PersonStatus.INACTIVE.equals(updatedPerson.getIdStatus())) {
                throw new ForbiddenException("person is inactive");
            }

            updatedPerson.setFirebaseUid(userRecord.getUid());
            updatedPerson.setName(userRecord.getDisplayName());
            updatedPerson.setEmail(userRecord.getEmail());
            updatedPerson.setPhoneNumber(userRecord.getPhoneNumber());
            updatedPerson.setPhotoUri(userRecord.getPhotoUrl());
            updatedPerson.setIdStatus(PersonStatus.ACTIVE);
            updatedPerson = personRepository.save(updatedPerson);

            if (!StringUtils.isEmpty(idInstance)) {
                AppInstance appInstance = new AppInstance();

                Optional<AppInstance> optionalAppInstance = appInstanceRepository.findByIdInstance(idInstance);
                if (optionalAppInstance.isPresent()) {
                    appInstance = optionalAppInstance.get();
                }
                appInstance.setIdInstance(idInstance);
                appInstance.setPerson(updatedPerson);
                appInstanceRepository.save(appInstance);
            }
            return updatedPerson;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException();
        }
    }

    @Override
    public void signOut(String firebaseUid, String idInstance) {
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            UserRecord userRecord = firebaseAuth.getUser(firebaseUid);
            Optional<Person> optionalPerson = personRepository.findByFirebaseUid(firebaseUid);

            Person updatedPerson = null;
            if (optionalPerson.isPresent()) {
                updatedPerson = optionalPerson.get();
                for (AppInstance appInstance : updatedPerson.getAppInstanceList()) {
                    if (appInstance.getIdInstance().equals(idInstance)) {
                        appInstanceRepository.deleteById(appInstance.getId());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException();
        }
    }

    @Override
    public Person getPersonByFirebaseUid(String firebaseUid) {
        Optional<Person> optionalPerson = personRepository.findByFirebaseUid(firebaseUid);
        if (optionalPerson.isPresent()) {
            return optionalPerson.get();
        } else {
            throw new NotFoundException("Person not exist");
        }
    }

    @Override
    public Person getPersonById(Integer idPerson) {
        Optional<Person> optionalPerson = personRepository.findById(idPerson);
        if (optionalPerson.isPresent()) {
            return optionalPerson.get();
        } else {
            throw new NotFoundException("Person not found");
        }
    }


}
