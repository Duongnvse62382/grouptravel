package com.fpt.gta.rest.managetrip;

import com.fpt.gta.rest.manageplan.PlanDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class TripDTO {
    private Integer id;
    private Timestamp startAt;
    private Timestamp endAt;
    private Timestamp startUtcAt;
    private Timestamp endUtcAt;
    private PlaceDTO endPlace;
    private Timestamp voteEndAt;
    private PlanDTO electedPlan;

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
