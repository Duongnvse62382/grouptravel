package com.fpt.gta.algorithm.backtracking;

public class Point {
    String name;
    double latitude;
    double longitude;

    public Point(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public double distance(Point point) {
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        double lat1 = Math.toRadians(getLatitude());
        double lon1 = Math.toRadians(getLongitude());
        double lat2 = Math.toRadians(point.getLatitude());
        double lon2 = Math.toRadians(point.getLongitude());

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
        return name;
    }
}

