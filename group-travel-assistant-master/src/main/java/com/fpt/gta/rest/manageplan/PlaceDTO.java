package com.fpt.gta.rest.manageplan;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlaceDTO {
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