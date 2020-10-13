package com.fpt.gta.algorithm.backtracking;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class BackTracking {

    private double CURRENT_MIN_DIST = Double.MAX_VALUE;

    private static double minDistToFinish(Set<Point> todoPoints) {
        double distToFinish = 0;
        for (Point p1 : todoPoints) {
            double d = Double.MAX_VALUE;
            for (Point p2 : todoPoints) {
                if (p1.equals(p2))
                    continue;
                d = Math.min(d, p1.distance(p2));
            }
            distToFinish += d;
        }
        return distToFinish;
    }

    public void findBestRoute(Set<Point> todoPoints, List<Point> donePoints, double dist) {
        if (todoPoints.isEmpty()) {
            dist += donePoints.get(0).distance(donePoints.get(donePoints.size() - 1));
            CURRENT_MIN_DIST = Math.min(CURRENT_MIN_DIST, dist);
            String orderedPlaces = String.join("-> ", donePoints.stream().map(Point::getName).collect(toSet()));
            System.out.println(orderedPlaces);
        } else {
            for (Point nextPoint : todoPoints) {
                // add point
                donePoints.add(nextPoint);

                // update distance
                dist += (donePoints.size() > 1 ? donePoints.get(donePoints.size() - 2)
                        .distance(donePoints.get(donePoints.size() - 1)) : 0);

                // we do not recurse if the current distance is already bigger than a known minimum
                if (dist > CURRENT_MIN_DIST)
                    continue;

                if (dist + minDistToFinish(todoPoints) > CURRENT_MIN_DIST)
                    continue;
                // recurse
                Set<Point> tmp = new HashSet<>(todoPoints);
                tmp.remove(nextPoint);
                findBestRoute(tmp, donePoints, dist);
                // remove point
                donePoints.remove(nextPoint);
            }
        }
    }
}
