package com.fpt.gta.data.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ActivityDTO implements Serializable, Comparable<ActivityDTO> {

    private Integer id;
    @SerializedName("idType")
    private Integer idType;
    @SerializedName("name")
    private String name;
    @SerializedName("startAt")
    private Date startAt;
    @SerializedName("endAt")
    private Date endAt;
    @SerializedName("startUtcAt")
    private Date startUtcAt;
    @SerializedName("endUtcAt")
    private Date endUtcAt;
    @SerializedName("estimatePrice")
    private BigDecimal estimatePrice;
    @SerializedName("endPlace")
    private PlaceDTO endPlace;
    @SerializedName("startPlace")
    private PlaceDTO startPlace;
    @SerializedName("isInPlan")
    private Boolean isInPlan;
    @SerializedName("isAdded")
    private Boolean isAdded;
    @SerializedName("isTooFar")
    private Boolean isTooFar = false;

    @SerializedName("documentList")
    private List<DocumentDTO> documentList = new ArrayList<>();

    @SerializedName("taskList")
    private List<TaskDTO> taskList = new ArrayList<>();

    @Override
    public int compareTo(ActivityDTO o) {
        return getStartUtcAt().compareTo(o.getStartUtcAt());
    }
}


