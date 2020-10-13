package com.fpt.gta.rest.mangeplace;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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
    private Boolean isTooFar = false;
    private List<PLaceImageDTO> placeImageList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaceDTO)) return false;
        PlaceDTO placeDTO = (PlaceDTO) o;
        return Objects.equals(googlePlaceId, placeDTO.googlePlaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(googlePlaceId);
    }


    @Data
    public static class PLaceImageDTO {
        private Integer id;
        private String uri;
        private Integer height;
        private Integer width;
    }
}
