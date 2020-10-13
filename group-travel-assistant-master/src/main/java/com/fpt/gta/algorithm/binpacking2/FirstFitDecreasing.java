package com.fpt.gta.algorithm.binpacking2;

import com.fpt.gta.algorithm.suggest.Record;
import com.fpt.gta.util.CustomListUtil;

import java.util.ArrayList;
import java.util.List;

public class FirstFitDecreasing {

    private List<Bin> bins = new ArrayList<>();
    private List<Integer> in = new ArrayList<>();

    public int getResult(List<Record> data, int binSize) {
        List<Record> in = CustomListUtil.getSortedListByTime(data);
//        Collections.sort(in, Collections.reverseOrder()); // sort input by size (big to small)
        bins.add(new Bin(binSize)); // add first bin
//        for (Integer currentItem : in) {
        for (Record currentItem : in) {
            // iterate over bins and try to put the item into the first one it fits into
            boolean putItem = false; // did we put the item in a bin?
            int currentBin = 0;
            while (!putItem) {
                if (currentBin == bins.size()) {
                    // item did not fit in last bin. put it in a new bin
                    Bin newBin = new Bin(binSize);
                    newBin.put(currentItem);
                    bins.add(newBin);
                    putItem = true;
                } else if (bins.get(currentBin).put(currentItem)) {
                    // item fit in bin
                    putItem = true;
                } else {
                    // try next bin
                    currentBin++;
                }
            }
        }
        return bins.size();
    }

    public static List<Bin> run(List<Record> data, int binSize) {
        List<Bin> bins = new ArrayList<>();
        List<Record> in = CustomListUtil.getSortedListByTime(data);
//        Collections.sort(in, Collections.reverseOrder()); // sort input by size (big to small)
        bins.add(new Bin(binSize)); // add first bin
//        for (Integer currentItem : in) {
        for (Record currentItem : in) {
            // iterate over bins and try to put the item into the first one it fits into
            boolean putItem = false; // did we put the item in a bin?
            int currentBin = 0;
            while (!putItem) {
                if (currentBin == bins.size()) {
                    // item did not fit in last bin. put it in a new bin
                    Bin newBin = new Bin(binSize);
                    newBin.put(currentItem);
                    bins.add(newBin);
                    putItem = true;
                } else if (bins.get(currentBin).put(currentItem)) {
                    // item fit in bin
                    putItem = true;
                } else {
                    // try next bin
                    currentBin++;
                }
            }
        }
        return bins;
    }

    public void printBestBins() {
        System.out.println("Bins:");
        if (bins.size() == in.size()) {
            System.out.println("each item is in its own bin");
        } else {
            for (Bin bin : bins) {
                System.out.println(bin.toString());
            }
        }
    }
}
