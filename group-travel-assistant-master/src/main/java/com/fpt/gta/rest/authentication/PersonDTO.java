package com.fpt.gta.rest.authentication;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

@Data
public class PersonDTO {
    private Integer id;
    private String name;
    private String email;
    private String phoneNumber;
    private String photoUri;
}