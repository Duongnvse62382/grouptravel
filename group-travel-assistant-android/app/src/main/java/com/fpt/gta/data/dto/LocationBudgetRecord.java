package com.fpt.gta.data.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationBudgetRecord {
    private String locationBudgetName;
    private float totalRecord;
    private int locationImage;
}
