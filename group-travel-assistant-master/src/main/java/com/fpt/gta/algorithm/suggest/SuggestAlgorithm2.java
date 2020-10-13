package com.fpt.gta.algorithm.suggest;

import com.fpt.gta.algorithm.binpacking2.Bin;
import com.fpt.gta.algorithm.binpacking2.FirstFitDecreasing;
import com.fpt.gta.util.CustomListUtil;
import lombok.NoArgsConstructor;
import weka.clusterers.SimpleKMeans;
import weka.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class SuggestAlgorithm2 {


    public static List<Record> data = new ArrayList<>();

    public Map<Integer, List<Record>> preparePlan2(int k) {
        data.clear();
        generateData();
        Map<Integer, List<Record>> map = new HashMap<>();
        if (k > 1) {
            for (int i = 0; i <= k; i++) {
                map.put(i, new ArrayList<>());
            }
        }
        return map;
    }

    public Map<Integer, List<Record>> preparePlan(int k) {
        Map<Integer, List<Record>> map = new HashMap<>();
        for (int i = 0; i <= k; i++) {
            map.put(i, new ArrayList<>());
        }
        return map;
    }

    public Map<Integer, List<Record>> run(Map<Integer, List<Record>> map,
                                          List<Record> data, int k,
                                          int key, int iteration,
                                          DistanceFunction distanceFunction,
                                          int DAY_CAPACITY)
            throws Exception {
//        System.out.println("Data KMeans:" + data.size());
        System.out.println("Loop:" + key);
        if (k > 1) {
            Instances places = prepareData(data, key);
            // start KMeans
            SimpleKMeans kMeans = new SimpleKMeans();
            kMeans.setPreserveInstancesOrder(true);
            kMeans.setSeed(k * 2);
            kMeans.setMaxIterations(iteration);
            kMeans.setDistanceFunction(distanceFunction);
            kMeans.setNumClusters(k);
            kMeans.buildClusterer(places);
            //list Cluster
//            Instances clusters = kMeans.getClusterCentroids();
            for (int i = 0; i < places.numInstances(); i++) {
                int clusterNumber = kMeans.clusterInstance(places.instance(i));
                Record r = data.get(i);
                r.setClusterNumber(clusterNumber + key);
                r.setIsInPlan(true);
                data.set(i, r);
                List<Record> list = map.get(clusterNumber + key);
                list.add(r);
                map.put(clusterNumber + key, list);
            }
            // end KMeans
            // start Bin Packing
            int maxKey = CustomListUtil.getMaxKeyValue(map);
            double totalPlaceSpendTime = map.get(maxKey).stream().mapToInt(Record::getTimeSpent).sum();

            if (totalPlaceSpendTime > DAY_CAPACITY) {
                System.out.println("run Bin packing " + key);
                List<Bin> listBinPacking = FirstFitDecreasing.run(map.get(maxKey), DAY_CAPACITY);
                int indexOfBin = CustomListUtil.getBiggestBin(listBinPacking);
                List<Record> listAfterBinPacking = listBinPacking.get(indexOfBin).getItems();
                int finalKey = key;
                listAfterBinPacking.forEach(e -> {
                    e.setIsInPlan(true);
                    e.setClusterNumber(finalKey);
                });
                map.put(key, listAfterBinPacking);
                for (Record r : listAfterBinPacking) {
                    data.remove(r);
                }
                int i = key + 1;
                while (i <= k) {
                    map.put(i, new ArrayList<>());
                    i++;
                }
                key++;
                k--;
                run(map, data, k, key, iteration, distanceFunction, DAY_CAPACITY);
            } else {
                System.out.println("Du Thoi gian sau khi chay KMeans");
            }
        } else {
//            System.out.println();
            System.out.println("Last loop: key:" + key + ", k: " + k);
            List<Bin> listBinPacking = FirstFitDecreasing.run(data, DAY_CAPACITY);
            int indexOfBin = CustomListUtil.getBiggestBin(listBinPacking);
            System.out.println("Index of Bin: " + indexOfBin);
            List<Record> listAfterBinPacking = listBinPacking.get(indexOfBin).getItems();
            int finalKey = key;
            listAfterBinPacking.forEach(e -> {
                e.setIsInPlan(true);
                e.setClusterNumber(finalKey);
            });
            map.put(key, listAfterBinPacking);

            List<Record> leftOutRecord = new ArrayList<>();

            for (Bin bin : listBinPacking) {
                if (listBinPacking.indexOf(bin) != indexOfBin) {
                    bin.getItems().forEach(r -> {
                        r.setClusterNumber(finalKey + 1);
                        r.setIsInPlan(false);
                    });
                    leftOutRecord.addAll(bin.getItems());
                }
            }
            List<Record> temp = map.get(key + 1);
            if (!leftOutRecord.isEmpty()) {
                if (map.get(key + 1).isEmpty()) {
                    map.put(key + 1, leftOutRecord);
                } else {
                    temp.addAll(leftOutRecord);
                    map.put(key + 1, temp);
                }
            }
        }
        // end bin packing
        return map;
    }

    public static Map<Integer, List<Record>> makePlanForStartDay(Map<Integer, List<Record>> map, int k,
                                                                 int START_CAPACITY) throws Exception {
        Map<Integer, List<Record>> mapStart = new HashMap<>();
        map.forEach((key, value) -> {
            try {
                if (!value.isEmpty() && key != k) {
                    List<Bin> listBinPacking = FirstFitDecreasing.run(value, START_CAPACITY);
                    int indexOfBin = CustomListUtil.getBiggestBin(listBinPacking);

                    List<Record> listAfterBinPacking = listBinPacking.get(indexOfBin).getItems();
                    mapStart.put(key, listAfterBinPacking);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        int mapAfterBinPackingMaxKey = CustomListUtil.getMaxKeyValue(mapStart);
        List<Record> r1 = mapStart.get(mapAfterBinPackingMaxKey);
        for (Record record : r1) {
            record.setClusterNumber(0);
        }
        List<Record> r3 = map.get(mapAfterBinPackingMaxKey);
        r3.removeAll(r1);
        r3.forEach(r -> {
            r.setClusterNumber(k);
            r.setIsInPlan(false);
        });
        if (mapAfterBinPackingMaxKey != 0) {
            List<Record> r2 = map.get(0);
            r2.forEach(record -> record.setClusterNumber(mapAfterBinPackingMaxKey));
            map.put(0, r1);
            map.put(mapAfterBinPackingMaxKey, r2);
        } else {
            map.put(0, r1);
        }
        if (map.get(k).isEmpty()) {
            map.put(k, r3);
        } else {
            map.get(k).addAll(r3);
        }
        return map;
    }

    public static Map<Integer, List<Record>> makePlanForEndDay(Map<Integer, List<Record>> map, int k,
                                                               int END_CAPACITY) throws Exception {
        Map<Integer, List<Record>> mapEnd = new HashMap<>();
        map.forEach((key, value) -> {
            try {
//                if (!value.isEmpty() && key != k && key > 0) {
                if (key != k && key > 0) {
                    List<Bin> listBinPacking = FirstFitDecreasing.run(value, END_CAPACITY);
                    int indexOfBin = CustomListUtil.getBiggestBin(listBinPacking);
                    List<Record> listAfterBinPacking = listBinPacking.get(indexOfBin).getItems();
                    mapEnd.put(key, listAfterBinPacking);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        int mapAfterBinPackingMaxKey = CustomListUtil.getMaxKeyValue(mapEnd);
        List<Record> r1 = mapEnd.get(mapAfterBinPackingMaxKey);
        r1.forEach(record -> record.setClusterNumber(k - 1));
        List<Record> r3 = map.get(mapAfterBinPackingMaxKey);
        r3.removeAll(r1);
        r3.forEach(r -> {
            r.setClusterNumber(k);
            r.setIsInPlan(false);
        });
        if (mapAfterBinPackingMaxKey != (k - 1)) {
            List<Record> r2 = map.get(k - 1);
            r2.forEach(r -> r.setClusterNumber(mapAfterBinPackingMaxKey));
            map.put(k - 1, r1);
            map.put(mapAfterBinPackingMaxKey, r2);
        } else {
            map.put(k - 1, r1);
        }
        if (map.get(k).isEmpty()) {
            map.put(k, r3);
        } else {
            map.get(k).addAll(r3);
        }
        return map;
    }

    public static Instances prepareData(List<Record> data, int test) {
        FastVector allAttributes = new FastVector(2);
        Attribute lat = new Attribute("lat");
        Attribute lng = new Attribute("lng");
        allAttributes.addElement(lat);
        allAttributes.addElement(lng);
        Instances places = new Instances("test" + test, allAttributes, data.size());
        for (Record r : data) {
            Instance inst = new Instance(2);
            inst.setValue(lat, r.getLat());
            inst.setValue(lng, r.getLng());
            places.add(inst);
        }
        return places;
    }


    public static void generateData() {
        data.clear();
        data.add(createRecord("DinhBaoDai", 11.935728, 108.447872, 8));
        data.add(createRecord("Bánh ướt lòng gà Long", 11.948468, 108.436386, 4));
        data.add(createRecord("Lẩu Bò Khu Ba Toa - Quán Gỗ", 11.941382, 108.429452, 8));
        data.add(createRecord("Cơm Niêu Như Ngọc", 11.937663, 108.438364, 6));
        data.add(createRecord("Cafe Triệu đoá hồng", 11.94867, 108.431565, 7));
        data.add(createRecord("Doc Nha Bo", 11.931988, 108.432461, 5));
        data.add(createRecord("Pedagogical College of Đà Lạt", 11.945467, 108.452773, 7));
        data.add(createRecord("Cối Xay Gió", 11.941179, 108.431729, 8));
        data.add(createRecord("Bao Dai Summer Palace", 11.930145, 108.429588, 5));
        data.add(createRecord("Chùa Thiền Viện Chúc Lâm", 11.940419, 108.458313, 8));
        data.add(createRecord("Kem bơ Thanh Thảo", 11.947283, 108.43703, 5));
        data.add(createRecord("Xuan Huong Lake", 11.940736, 108.44154, 4));
        data.add(createRecord("Ga Da Lat", 11.941617, 108.454465, 4));
        data.add(createRecord("Tiem banh Lien Hoa", 11.942804, 108.435106, 6));
        data.add(createRecord("Quán Ốc Số 33", 11.943247, 108.432392, 4));
        data.add(createRecord("3D World Da Lat", 11.942420, 108.437869, 6));
        data.add(createRecord("Tran Le Xuan Palace", 11.945901, 108.426445, 7));
        data.add(createRecord("Big C Da Lat", 11.938358, 108.444319, 8));
        data.add(createRecord("Tiệm Bánh Cối Xay Gió", 11.944205, 108.435971, 7));
        data.add(createRecord("Crazy House", 11.934737, 108.430881, 7));
    }

    public static Record createRecord(String name, Double l1, Double l2, int time) {
        Record record = new Record();
        record.setName(name);
        record.setLat(l1);
        record.setLng(l2);
        record.setTimeSpent(time * 15);
        return record;
    }
}
