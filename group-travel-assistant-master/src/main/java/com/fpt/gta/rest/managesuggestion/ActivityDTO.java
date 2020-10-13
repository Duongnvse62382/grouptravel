package com.fpt.gta.rest.managesuggestion;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class ActivityDTO {

    private Integer id;
    private String name;
    private String startPlaceName;
    private String endPlaceName;
    private Timestamp startAt;
    private Timestamp endAt;
    private String startAddress;
    private String endAddress;
    private String startPhoneNumber;
    private String endPhoneNumber;
    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private BigDecimal endLatitude;
    private BigDecimal endLongitude;
    private BigDecimal estimatePrice;
    private String startPlaceLocalName;
    private String endPlaceLocalName;
    private String startTimeZone;
    private String endTimeZone;
    private PlaceDTO endPlace;
    private PlaceDTO startPlace;
    private Integer idType;
    private Integer idStatus;
    private Boolean isInPlan;
    private Boolean isAdded;
    private Boolean isTooFar=false;
    private List<DocumentDTO> documentList;
    private List<PlaceDTO.PLaceImageDTO> placeImageList;

    @Override
    public String toString() {
        return name + " (-) isAdded:" + isAdded + " (-) isPlan:" + isInPlan;
    }
}
