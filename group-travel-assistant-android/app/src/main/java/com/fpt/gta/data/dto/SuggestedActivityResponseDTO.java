package com.fpt.gta.data.dto;


import com.fpt.gta.webService.gson.Exclude;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class SuggestedActivityResponseDTO implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("estimatePrice")
    private String estimatePrice;
    @SerializedName("owner")
    private MemberDTO owner;
    @SerializedName("endPlace")
    private PlaceDTO endPlace;
    @SerializedName("startPlace")
    private PlaceDTO startPlace;
    @SerializedName("idType")
    private Integer idType;
    @SerializedName("isTooFar")
    private Boolean isTooFar = false;
    private List<VotedSuggestedActivityDTO> votedSuggestedActivityList = new ArrayList<>();

    @Exclude
    private boolean isSelected;




    @Data
    public static class VotedSuggestedActivityDTO implements Serializable {
        private Integer id;
        private MemberDTO member;
    }

    @Data
    public static class MemberDTO implements Serializable {
        private Integer id;
        private BigDecimal expectedPrice;
        private Integer idRole;
        private Integer idStatus;
        private PersonDTO person;
    }

    @Data
    public static class PersonDTO implements Serializable {
        private Integer id;
        private String name;
        private String email;
        private String phoneNumber;
        private String photoUri;
        private String firebaseUid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuggestedActivityResponseDTO that = (SuggestedActivityResponseDTO) o;
        return Objects.equals(endPlace, that.endPlace) &&
                Objects.equals(startPlace, that.startPlace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endPlace, startPlace);
    }
}
