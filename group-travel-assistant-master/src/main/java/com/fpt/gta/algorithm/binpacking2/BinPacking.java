package com.fpt.gta.algorithm.binpacking2;

import com.fpt.gta.algorithm.suggest.SuggestAlgorithm2;
import com.fpt.gta.util.CustomListUtil;

import java.util.List;

public class BinPacking {

    public static void run() {
        FirstFitDecreasing ffd = new FirstFitDecreasing();
        SuggestAlgorithm2.generateData();
        long startTime;
        long estimatedTime;

        startTime = System.currentTimeMillis();
        System.out.println("Need " + ffd.getResult(SuggestAlgorithm2.data, 470) + " bins");
        ffd.printBestBins();
        List<Bin> list = FirstFitDecreasing.run(SuggestAlgorithm2.data, 470);
        int index = list.indexOf(CustomListUtil.getBiggestBin(list));
        System.out.println(index);
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("in " + estimatedTime + " ms");
    }
}
