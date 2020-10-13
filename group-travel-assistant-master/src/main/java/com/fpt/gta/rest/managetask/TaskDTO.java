package com.fpt.gta.rest.managetask;

import com.fpt.gta.rest.managegroup.GroupDTO;
import com.fpt.gta.rest.manageplan.DocumentDTO;
import com.fpt.gta.rest.manageplan.PlaceDTO;
import com.fpt.gta.rest.managetrip.TripDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class TaskDTO {
    private Integer id;
    private String name;
    private Integer idStatus;
    private Integer order;
    private List<TaskAssignmentDTO> taskAssignmentList;
    private GroupDTO group;
    private TripDTO trip;
    private ActivityDTO activity;

    @Data
    public static class ActivityDTO {
        private Integer id;
        private String name;
        private Timestamp startAt;
        private Timestamp endAt;
        private Timestamp startUtcAt;
        private Timestamp endUtcAt;
        private BigDecimal estimatePrice;
        private PlaceDTO endPlace;
        private PlaceDTO startPlace;
        private Integer idType;
        private Integer idStatus;
        private List<DocumentDTO> documentList;
    }

    @Data
    public static class TaskAssignmentDTO {
        private Integer id;
        private MemberDTO member;
    }

    @Data
    public static class MemberDTO {
        private Integer id;
        private BigDecimal expectedPrice;
        private Integer idRole;
        private Integer idStatus;
        private Boolean isReady;
        private com.fpt.gta.rest.managegroup.MemberDTO.PersonDTO person;


    }

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
