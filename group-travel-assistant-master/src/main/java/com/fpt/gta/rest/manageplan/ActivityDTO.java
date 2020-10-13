package com.fpt.gta.rest.manageplan;

import com.fpt.gta.rest.managetask.TaskDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class ActivityDTO {
    private Integer id;
    private String name;
    private Timestamp startAt;
    private Timestamp endAt;
    private Timestamp startUtcAt;
    private Timestamp endUtcAt;
    private BigDecimal estimatePrice;
    private PlaceDTO endPlace;
    private PlaceDTO startPlace;
    private Integer idType;
    private Integer idStatus;
    private List<DocumentDTO> documentList;
    private List<TaskDTO> taskList;
    private Boolean isTooFar = false;

}




