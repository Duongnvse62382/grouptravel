package com.fpt.gta.data.dto;

import com.fpt.gta.webService.gson.Exclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class GroupResponseDTO implements Serializable, Comparable<GroupResponseDTO> {

    @SerializedName("id")
    Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("startAt")
    private Date startAt;
    @SerializedName("endAt")
    private Date endAt;
    @SerializedName("invitationCode")
    private String invitationCode;
    @SerializedName("startPlace")
    private PlaceDTO startPlace;
    @SerializedName("currency")
    private CurrencyDTO currency;
    @SerializedName("startUtcAt")
    private Date startUtcAt;
    @SerializedName("endUtcAt")
    private Date endUtcAt;
    @SerializedName("idStatus")
    private Integer idStatus;
    @Exclude
    private Calendar calendarStart;
    @Exclude
    private Calendar calendarEnd;
    @SerializedName("activityBudget")
    private BigDecimal activityBudget = BigDecimal.ZERO;
    @SerializedName("accommodationBudget")
    private BigDecimal accommodationBudget = BigDecimal.ZERO;
    @SerializedName("transportationBudget")
    private BigDecimal transportationBudget = BigDecimal.ZERO;
    @SerializedName("foodBudget")
    private BigDecimal foodBudget = BigDecimal.ZERO;

    private List<TripReponseDTO>  tripList = new ArrayList<>();

    private List<MemberDTO>  memberList = new ArrayList<>();

    @Override
    public int compareTo(GroupResponseDTO o) {
        return getStartUtcAt().compareTo(o.getStartUtcAt());
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
}
