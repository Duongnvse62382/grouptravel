package com.fpt.gta.rest.manageplan;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TripDTO {
    private Integer id;
    private Timestamp startAt;
    private Timestamp endAt;
    private PlaceDTO endPlace;
    private Timestamp voteEndAt;
    private Timestamp startUtcAt;
    private Timestamp endUtcAt;
}
