package com.fpt.gta.util;

import com.fpt.gta.algorithm.binpacking2.Bin;
import com.fpt.gta.algorithm.suggest.LeftOut;
import com.fpt.gta.algorithm.suggest.Record;
import com.fpt.gta.model.entity.SuggestedActivity;
import com.fpt.gta.rest.managesuggestion.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class CustomListUtil {
    private static final int SECOND = 1;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static SuggestionService suggestionService;

    @Autowired
    public CustomListUtil(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    public static int getMaxKeyValue(Map<Integer, List<Record>> map) {
        return Collections.max(map.entrySet(),
                (o1, o2) -> o1.getValue().stream().mapToInt(r -> r.getTimeSpent()).sum() >
                        o2.getValue().stream().mapToInt(r -> r.getTimeSpent()).sum() ? 1 : -1)
                .getKey();
    }

    public static int getMaxSpendTime(Map<Integer, List<Record>> map) {
        return map.values().stream().mapToInt(i ->
                i.stream().mapToInt(r ->
                        r.getTimeSpent()).sum()).sum();
    }

    public static List<Record> convertSetToList(Set<Record> set) {
        // create an empty list
        List<Record> list = new ArrayList<>();

        // push each element in the set into the list
        for (Record t : set)
            list.add(t);

        // return the list
        return list;
    }

    public static Set<Record> convertListToSet(List<Record> list) {
        // create an empty list
        Set<Record> set = new HashSet<>();

        // push each element in the set into the list
        for (Record t : list)
            set.add(t);

        // return the list
        return set;
    }

    public static double getDistance(List<Record> list) {
        double total = 0;
        // create an empty list
        // push each element in the set into the list
        for (int i = 0; i < list.size() - 1; i++) {
            total += Record.calculateDistance(list.get(i), list.get(i + 1));
        }
        return total;
    }

    public static int getMinKeyValue(Map<Integer, List<Record>> map) {
        return Collections.max(map.entrySet(),
                (o1, o2) -> o1.getValue().stream().mapToInt(r -> r.getTimeSpent()).sum() >
                        o2.getValue().stream().mapToInt(r -> r.getTimeSpent()).sum() ? -1 : 1)
                .getKey();
    }

    public static int getBiggestBin(List<Bin> list) {
        Optional<Bin> optionalBin = list.stream().max(Comparator.comparing(Bin::getSumOfTime));
        if (optionalBin.isPresent()) {
            return list.indexOf(optionalBin.get());
        }
        return -1;
    }

    public static int getTotalTimeSpendLeftOut(List<LeftOut> list) {
        return list.stream().mapToInt(r -> r.getTimeSpent()).sum();
    }

    public static int getTotalTimeSpendRecord(List<Record> list) {
        return list.stream().mapToInt(r -> r.getTimeSpent()).sum();
    }

    public static List<Record> getSortedList(List<Record> list, Record r) {
        return list.stream().sorted((t1, t2) -> {
            if (Record.calculateDistance(t1, r) > Record.calculateDistance(t2, r)) {
                return 1;
            } else {
                return -1;
            }
        }).collect(Collectors.toList());
    }

    public static boolean checkListHasIsTooFar(List<Record> list) {
        boolean check = false;
        for (Record r : list) {
            if (r.getIsTooFar()) {
                check = true;
                break;
            }
        }
        return check;
    }

    public static boolean checkListOnlyHasIsTooFar(List<SuggestedActivity> list) {
        boolean check = true;
        for (int i = 0; i < list.size(); i++) {
            SuggestedActivity index = list.get(i);
            if (!index.getIsTooFar()) {
                check = index.getIsTooFar();
                break;
            }
        }
        return check;
    }

    public static List<Record> getSortedListAsc(List<Record> list, Record r) {
        return list.stream().sorted((t1, t2) -> {
            if (Record.calculateDistance(t1, r) < Record.calculateDistance(t2, r)) {
                return 1;
            } else {
                return -1;
            }
        }).collect(Collectors.toList());
    }

    public static void checkDistance(List<Record> list, Record r) {
        for (int i = list.size(); i-- > 0; ) {
            Record index = list.get(i);
            if (suggestionService.getTravelTime(index, r) > 2 * HOUR) {
                index.setIsTooFar(true);
            } else {
                break;
            }
        }
    }

    public static Record getCenterRecord(List<Record> list) {
        Record center = new Record();
        if (!list.isEmpty()) {
            double lat = list.stream().mapToDouble(i -> i.getLat()).sum() / list.size();
            double lng = list.stream().mapToDouble(i -> i.getLng()).sum() / list.size();
            center.setLat(lat);
            center.setLng(lng);
            System.out.println("Lat :" + lat + "- Lng :" + lng);
        }
        return center;
    }

    public static List<Record> getSortedListByTime(List<Record> list) {
        return list.stream().sorted((t1, t2) -> {
            if (t1.getTimeSpent() <= t2.getTimeSpent()) {
                return 1;
            } else {
                return -1;
            }
        }).collect(Collectors.toList());
    }

    public static List<Record> getSystemAddPlaces(List<Record> list1, List<Record> list2,
                                                  int capacity) {
        int sum = 0;
        List<Record> temp = new ArrayList<>();
        if (!list2.isEmpty()) {
            for (Record record : list2) {
                if (!record.getIsTooFar()) {
                    if ((sum + record.getTimeSpent()) < capacity) {
                        record.setIsAdded(true);
                        record.setIsInPlan(true);
                        list1.add(record);
                        temp.add(record);
                        sum += record.getTimeSpent();
                    } else {
                        break;
                    }
                }
            }
            list2.removeAll(temp);
        }
        return list1;
    }

    public static List<Record> addPlaceFromLeftOut(List<Record> list1, List<Record> list2,
                                                   int capacity) {
        int sum = 0;
        List<Record> temp = new ArrayList<>();
        if (!list2.isEmpty()) {
            for (Record record : list2) {
                if (!record.getIsTooFar()) {
                    if ((sum + record.getTimeSpent()) < capacity) {
                        record.setIsInPlan(true);
                        list1.add(record);
                        temp.add(record);
                        sum += record.getTimeSpent();
                    } else {
                        break;
                    }
                }
            }
            list2.removeAll(temp);
        }
        return list1;
    }

    public static Set<Record> addPlacesToSet(Set<Record> set, List<Record> list2,
                                             int numberOfPlaces) {
        list2.removeAll(set);
        List<Record> temp = new ArrayList<>();
        int count = 0;
        for (Record record : list2) {
            if (count < numberOfPlaces) {
                record.setIsAdded(true);
                set.add(record);
                temp.add(record);
                count++;
            } else {
                break;
            }
        }
        list2.removeAll(temp);
        return set;
    }
}
