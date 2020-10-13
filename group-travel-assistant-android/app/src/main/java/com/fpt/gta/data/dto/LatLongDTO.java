package com.fpt.gta.data.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class LatLongDTO implements Serializable {
    private String placeName;
    private Double lattitude;
    private Double longtitue;

    public LatLongDTO(String placeName, Double lattitude, Double longtitue) {
        this.placeName = placeName;
        this.lattitude = lattitude;
        this.longtitue = longtitue;
    }

    public LatLongDTO() {
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLongtitue() {
        return longtitue;
    }

    public void setLongtitue(Double longtitue) {
        this.longtitue = longtitue;
    }
}
