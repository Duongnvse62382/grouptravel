package com.fpt.gta.algorithm.suggest;

import com.fpt.gta.rest.managesuggestion.DocumentDTO;
import com.fpt.gta.rest.managesuggestion.PlaceDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Record {

    private String name;
    private double lat;
    private double lng;
    private int timeSpent;
    private String placeName;
    private String placeAddress;
    private String placePhoneNumber;
    private BigDecimal placeLat;
    private BigDecimal placeLong;
    private String placeLocalName;
    private String placeTimeZone;
    private PlaceDTO placeDTO;
    private Integer idType;
    private Integer idStatus;
    private int clusterNumber;
    private Boolean isInPlan = false;
    private Boolean isAdded;
    private Boolean isTooFar = false;
    private List<DocumentDTO> documentList;
    private List<PlaceDTO.PLaceImageDTO> placeImageList;

    public static Record createCluster(Double l1, Double l2) {
        Record record = new Record();
        record.setLat(l1);
        record.setLng(l2);
        return record;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;
        Record record = (Record) o;
        return Double.compare(record.lat, lat) == 0 &&
                Double.compare(record.lng, lng) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }

    public static double calculateDistance(Record r1, Record r2) {
        double lat1 = Math.toRadians(r1.getLat());
        double lat2 = Math.toRadians(r2.getLat());
        double lon1 = Math.toRadians(r1.getLng());
        double lon2 = Math.toRadians(r2.getLng());
        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }

    @Override
    public String toString() {
        return name + " (-) isTooFar:" + isTooFar + " (-) isAdded:" + isAdded;
    }
}

