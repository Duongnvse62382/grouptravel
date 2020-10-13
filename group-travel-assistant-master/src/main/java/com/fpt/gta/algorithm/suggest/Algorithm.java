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
public class Algorithm {


    public static List<Record> data = new ArrayList<>();

    public Map<Integer, List<Record>> preparePlan(int k) {
        generateData();
        Map<Integer, List<Record>> map = new HashMap<>();
        if (k > 1) {
            for (int i = 0; i <= k; i++) {
                map.put(i, new ArrayList<>());
            }
        }
        return map;
    }

    public Map<Integer, List<Record>> run(Map<Integer, List<Record>> map, List<Record> data, int k,
                                          int key, int iteration, DistanceFunction distanceFunction,
                                          int DAY_CAPACITY) throws Exception {
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
                while (i < k) {
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
            List<Bin> listBinPacking = FirstFitDecreasing.run(data, DAY_CAPACITY);
            int indexOfBin = CustomListUtil.getBiggestBin(listBinPacking);
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
            map.put(key + 1, leftOutRecord);
        }
        // end bin packing
        return map;
    }

    public Map<Integer, List<Record>> runKMeans(Map<Integer, List<Record>> map, List<Record> data,
                                                int k, int iteration, int key,
                                                DistanceFunction distanceFunction) throws Exception {
        Instances places = prepareData(data);
        // start KMeans
        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setPreserveInstancesOrder(true);
        kMeans.setSeed(k * 2);
        kMeans.setMaxIterations(iteration);
        kMeans.setDistanceFunction(distanceFunction);
        kMeans.setNumClusters(k);
        kMeans.buildClusterer(places);
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

        return map;
    }

    public Map<Integer, List<Record>> runBinPacking(Map<Integer, List<Record>> map,
                                                    int DAY_CAPACITY, int key, int k) {
        int maxKey = CustomListUtil.getMaxKeyValue(map);
        double totalPlaceSpendTime = map.get(maxKey).stream().mapToInt(Record::getTimeSpent).sum();
        if (totalPlaceSpendTime > DAY_CAPACITY) {
            List<Bin> listBinPacking = FirstFitDecreasing.run(map.get(maxKey), DAY_CAPACITY);
            int indexOfBin = CustomListUtil.getBiggestBin(listBinPacking);
            List<Record> listAfterBinPacking = listBinPacking.get(indexOfBin).getItems();
            listAfterBinPacking.forEach(e -> {
                e.setClusterNumber(key);
            });
            map.put(key, listAfterBinPacking);
            for (Record r : listAfterBinPacking) {
                data.remove(r);
            }
            int i = key + 1;
            while (i < k) {
                map.put(i, new ArrayList<>());
                i++;
            }
        }
        return map;
    }

    public Map<Integer, List<Record>> lastBinPacking(Map<Integer, List<Record>> map, int DAY_CAPACITY, int key) {
        List<Bin> listBinPacking = FirstFitDecreasing.run(data, DAY_CAPACITY);
        int indexOfBin = CustomListUtil.getBiggestBin(listBinPacking);
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
        map.put(key + 1, leftOutRecord);
        return map;
    }

    public Map<Integer, List<Record>> addRemainingToMap(Map<Integer, List<Record>> map, int key) {
        data.stream().forEach(r -> r.setClusterNumber(key + 1));
        map.put(key + 1, data);
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

    public static Instances prepareData(List<Record> data) {
        FastVector allAttributes = new FastVector(2);
        Attribute lat = new Attribute("lat");
        Attribute lng = new Attribute("lng");
        allAttributes.addElement(lat);
        allAttributes.addElement(lng);
        Instances places = new Instances("test", allAttributes, data.size());
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
        data.add(createRecord("Bánh ướt lòng gà Long", 11.948468, 108.436386, 8));
        data.add(createRecord("Cafe Triệu đoá hồng", 11.94867, 108.431565, 8));
        data.add(createRecord("Quán Ốc Số 33", 11.943247, 108.432392, 8));
        data.add(createRecord("Kem bơ Thanh Thảo", 11.947283, 108.43703, 8));
        data.add(createRecord("DinhBaoDai", 11.935728, 108.447872, 8));
        data.add(createRecord("Lẩu Bò Khu Ba Toa - Quán Gỗ", 11.941382, 108.429452, 8));
        data.add(createRecord("Cơm Niêu Như Ngọc", 11.937663, 108.438364, 8));
        data.add(createRecord("Doc Nha Bo", 11.931988, 108.432461, 8));
        data.add(createRecord("Cối Xay Gió", 11.941179, 108.431729, 8));
        data.add(createRecord("Cung Nam Phương Hoàng Hậu", 11.939964, 108.460493, 8));
        data.add(createRecord("Ga Da Lat", 11.94161007, 108.454465, 8));
        data.add(createRecord("Pedagogical College of Đà Lạt", 11.945467, 108.452773, 8));
        data.add(createRecord("Big C Da Lat", 11.938358, 108.444319, 8));
        data.add(createRecord("Tiệm Bánh Cối Xay Gió", 11.944205, 108.435971, 8));
        data.add(createRecord("Tiem banh Lien Hoa", 11.942804, 108.435106, 8));
        data.add(createRecord("Chùa Thiền Viện Chúc Lâm", 11.940419, 108.458313, 8));
        data.add(createRecord("An Cafe", 11.941721, 108.433854, 8));
        data.add(createRecord("Bao Dai Summer Palace", 11.930145, 108.429588, 8));
        data.add(createRecord("Crazy House", 11.934737, 108.430881, 8));
//        data.add(createRecord("Xuan Huong Lake", 11.940736, 108.44154, 5));
//        data.add(createRecord("Tran Le Xuan Palace", 11.945901, 108.426445, 7));
//        data.add(createRecord("3D World Da Lat", 11.942420, 108.437869, 6));
        System.out.println(data.size());
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
