package com.fpt.gta.data.dto;

import com.fpt.gta.webService.gson.Exclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Data;

@Data
public class PlaceDTO implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("googlePlaceId")
    private String googlePlaceId;
    @SerializedName("name")
    private String name;
    @SerializedName("timeZone")
    private String timeZone;
    @SerializedName("firebaseUid")
    private String firebaseUid;
    @SerializedName("address")
    private String address;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("localName")
    private String localName;
    @SerializedName("latitude")
    private BigDecimal latitude;
    @SerializedName("longitude")
    private BigDecimal longitude;
    @SerializedName("estimateSpendingHour")
    private Integer estimateSpendingHour;
    @SerializedName("isTooFar")
    private Boolean isTooFar = false;

    private List<PLaceImageDTO> placeImageList = new ArrayList<>();

    @Exclude
    private boolean isSelected;

    @Data
    public static class PLaceImageDTO implements Serializable{
        private Integer id;
        private String uri;
        private Integer height;
        private Integer width;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceDTO placeDTO = (PlaceDTO) o;
        return Objects.equals(googlePlaceId, placeDTO.googlePlaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(googlePlaceId);
    }
}
