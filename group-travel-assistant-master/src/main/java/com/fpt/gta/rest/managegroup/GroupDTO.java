package com.fpt.gta.rest.managegroup;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fpt.gta.rest.managebudget.TripDTO;
import com.fpt.gta.rest.managecurrency.CurrencyDTO;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.List;

@Data
public class GroupDTO {
    private Integer id;
    private String name;
    private Timestamp startAt;
    private Timestamp endAt;
    private Timestamp startUtcAt;
    private Timestamp endUtcAt;
    private Timestamp startZonedAt;
    private Timestamp endZonedAt;
    private PlaceDTO startPlace;
    private String invitationCode;
    private List<MemberDTO> memberList;
    private CurrencyDTO currency;
    private List<TripDTO> tripList;
    private Integer idStatus;
    private BigDecimal activityBudget = BigDecimal.ZERO;
    private BigDecimal accommodationBudget = BigDecimal.ZERO;
    private BigDecimal transportationBudget = BigDecimal.ZERO;
    private BigDecimal foodBudget = BigDecimal.ZERO;

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
