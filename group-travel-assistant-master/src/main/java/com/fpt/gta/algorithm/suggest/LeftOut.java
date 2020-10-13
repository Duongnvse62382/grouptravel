package com.fpt.gta.algorithm.suggest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeftOut {
    private String name;
    private double lat;
    private double lng;
    private int timeSpent;
    private int clusterNumber;
}
