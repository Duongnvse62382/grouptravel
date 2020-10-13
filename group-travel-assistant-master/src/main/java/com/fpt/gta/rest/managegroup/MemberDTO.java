package com.fpt.gta.rest.managegroup;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberDTO {
    private Integer id;
    private BigDecimal expectedPrice;
    private Integer idRole;
    private Integer idStatus;
    private PersonDTO person;
    private Boolean isReady;

    @Data
    public static class PersonDTO {
        private Integer id;
        private String name;
        private String email;
        private String phoneNumber;
        private String photoUri;
        private String firebaseUid;
    }

}
