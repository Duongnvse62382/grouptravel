package com.fpt.gta.data.dto;

import com.fpt.gta.webService.gson.Exclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TaskDTO implements Serializable, Comparable<TaskDTO>{
    private Integer id;
    private String name;
    private Integer order;
    private Integer idStatus;
    private List<TaskAssignmentDTO> taskAssignmentList = new ArrayList<>();
    private ActivityDTO activity;
    private GroupResponseDTO group;
    private TripReponseDTO trip;

    @Override
    public int compareTo(TaskDTO o) {
        return order.compareTo(o.order);
    }


    @Data
    public static class TaskAssignmentDTO implements Serializable{
        private Integer id;
        private MemberDTO member;
    }

    @Exclude
    private boolean isSelected;


}
