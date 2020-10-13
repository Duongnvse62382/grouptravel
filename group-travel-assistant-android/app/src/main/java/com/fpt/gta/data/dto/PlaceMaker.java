package com.fpt.gta.data.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PlaceMaker {
    private BigDecimal latitude;
    private BigDecimal longitude;
}
