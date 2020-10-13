package com.fpt.gta.rest.managesuggestedactivity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data

public class SuggestedActivityDTO {

    private Integer id;
    private String name;
    private BigDecimal estimatePrice;
    private MemberDTO owner;
    private PlaceDTO endPlace;
    private PlaceDTO startPlace;
    private Integer idType;
    private Boolean isTooFar=false;
    private List<VotedSuggestedActivityDTO> votedSuggestedActivityList = new ArrayList<>();

    @Data

    public static class VotedSuggestedActivityDTO {
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
        private PersonDTO person;
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

    @Data

    public static class PlaceDTO {
        private Integer id;
        private String googlePlaceId;
        private String name;
        private String address;
        private String phoneNumber;
        private String localName;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private Integer estimateSpendingHour;
        private String timeZone;
        private List<com.fpt.gta.rest.mangeplace.PlaceDTO.PLaceImageDTO> placeImageList;

    }


}
