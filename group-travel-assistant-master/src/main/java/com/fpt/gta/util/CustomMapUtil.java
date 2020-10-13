package com.fpt.gta.util;

import com.fpt.gta.algorithm.suggest.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomMapUtil {
    public static Map<Integer, List<Record>> addTooFarPlacesToLeftOut(Map<Integer, List<Record>> map
            , int k, Set<Record> list) {
        List<Record> isTooFarList = new ArrayList<>();
        list.forEach(r -> {
            if (r.getIsTooFar()) {
                isTooFarList.add(r);
            }
        });
        list.removeAll(isTooFarList);
        map.put(k, isTooFarList);
        return map;
    }
}
