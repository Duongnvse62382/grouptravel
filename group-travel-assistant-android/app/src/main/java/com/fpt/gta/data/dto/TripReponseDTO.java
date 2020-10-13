package com.fpt.gta.data.dto;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import lombok.Data;

@Data
public class TripReponseDTO implements Serializable, Comparable<TripReponseDTO> {
    @SerializedName("id")
    Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("startAt")
    private Date startAt;
    @SerializedName("endAt")
    private Date endAt;
    @SerializedName("endPlace")
    private PlaceDTO startPlace;
    @SerializedName("voteEndAt")
    private Date voteEndAt;
    @SerializedName("startUtcAt")
    private Date startUtcAt;
    @SerializedName("endUtcAt")
    private Date endUtcAt;
    @SerializedName("electedPlan")
    private PlanDTO electedPlan;

    @Override
    public int compareTo(TripReponseDTO o) {
        return getStartUtcAt().compareTo(o.getStartUtcAt());
    }
}
