package com.fpt.gta.algorithm.backtracking;

import com.fpt.gta.algorithm.suggest.Record;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class BackTracking2 {

    private double CURRENT_MIN_DIST = Double.MAX_VALUE;

    private double minDistToFinish(Set<Record> todoPoints) {
        double distToFinish = 0;
        for (Record p1 : todoPoints) {
            double d = Double.MAX_VALUE;
            for (Record p2 : todoPoints) {
                if (p1.equals(p2))
                    continue;
                d = Math.min(d, Record.calculateDistance(p1, p2));
            }
            distToFinish += d;
        }
        return distToFinish;
    }

    public void findBestRoute(Set<Record> todoPoints, List<Record> donePoints, double dist) {
        if (todoPoints.isEmpty()) {
            dist += Record.calculateDistance(donePoints.get(0), donePoints.get(donePoints.size() - 1));
            CURRENT_MIN_DIST = Math.min(CURRENT_MIN_DIST, dist);
            String orderedPlaces = String.join("-> ", donePoints.stream().
                    map(Record::getName).collect(toSet()));
            System.out.println(orderedPlaces);
            System.out.println(CURRENT_MIN_DIST);
        } else {
            for (Record nextPoint : todoPoints) {
                // add point
                donePoints.add(nextPoint);

                // update distance
                dist += (donePoints.size() > 1 ?
                        Record.calculateDistance(donePoints.get(donePoints.size() - 2),
                                donePoints.get(donePoints.size() - 1)) : 0);

                // we do not recurse if the current distance is already bigger than a known minimum
                if (dist > CURRENT_MIN_DIST)
                    continue;

                if (dist + minDistToFinish(todoPoints) > CURRENT_MIN_DIST)
                    continue;
                // recurse
                Set<Record> tmp = new HashSet<>(todoPoints);
                tmp.remove(nextPoint);
                findBestRoute(tmp, donePoints, dist);
                // remove point
                donePoints.remove(nextPoint);
            }
        }
    }
}
