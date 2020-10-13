package com.fpt.gta.feature.managetransaction.addtransaction.OCRTransaction;

import com.fpt.gta.data.dto.OcrDTO;
import com.fpt.gta.data.dto.SubsetSum;
import com.fpt.gta.data.dto.TransactionDTO;
import com.fpt.gta.feature.managetransaction.TransactionHandler;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

public final class OcrHandler {

    public static List<BigDecimal> prepareLines(List<BigDecimal> line) {
        List<BigDecimal> result = new ArrayList<>();
        List<BigDecimal> withoutDuplicate = new ArrayList<>();
        if (line.size() != 0) {
            for (BigDecimal bigDecimal : line) {
                if (bigDecimal.compareTo(BigDecimal.ZERO) > 0 && bigDecimal.compareTo(BigDecimal.valueOf(Double.parseDouble("200000000"))) == -1 && bigDecimal.compareTo(BigDecimal.ONE) == 1) {
                    result.add(bigDecimal);
                }
            }
            withoutDuplicate = result.stream()
                    .distinct()
                    .collect(Collectors.toList());
            Collections.sort(withoutDuplicate, Collections.reverseOrder());
        } else {
            throw new RuntimeException("Invalid Bill");
        }

        return withoutDuplicate;
    }


    public static List<OcrDTO> findOutTotalNumber(List<BigDecimal> withoutDuplicate, List<BigDecimal> bigDecimalList) {
        List<OcrDTO> nonDuplicateOCRList = new ArrayList<>();
        List<OcrDTO> result = new ArrayList<>();
        if (bigDecimalList.size() != 0 && withoutDuplicate.size() != 0) {

            //merge 2 list
            for (BigDecimal bigDecimal : withoutDuplicate) {
                OcrDTO ocrDTO1 = new OcrDTO();
                ocrDTO1.setBigDecimal(bigDecimal);
                ocrDTO1.setCount(0);
                nonDuplicateOCRList.add(ocrDTO1);
            }
            //find the largest count number dupplicated
            for (int i = 0; i < nonDuplicateOCRList.size(); i++) {

                if (nonDuplicateOCRList.get(i).getBigDecimal().remainder(BigDecimal.valueOf(100)).compareTo(BigDecimal.ZERO) == 0) {
                    nonDuplicateOCRList.get(i).setCount(nonDuplicateOCRList.get(i).getCount() + 1);
                }

                if (nonDuplicateOCRList.get(i).getBigDecimal().remainder(BigDecimal.valueOf(1000)).compareTo(BigDecimal.ZERO) == 0) {
                    nonDuplicateOCRList.get(i).setCount(nonDuplicateOCRList.get(i).getCount() + 1);
                }

                if (nonDuplicateOCRList.get(i).getBigDecimal().remainder(BigDecimal.valueOf(10000)).compareTo(BigDecimal.ZERO) == 0) {
                    nonDuplicateOCRList.get(i).setCount(nonDuplicateOCRList.get(i).getCount() + 1);
                }
            }

            if (nonDuplicateOCRList.size() != 0) {
                for (int i = 0; i < nonDuplicateOCRList.size(); i++) {
                    for (int j = 0; j < bigDecimalList.size(); j++) {
                        if (nonDuplicateOCRList.get(i).getBigDecimal().compareTo(bigDecimalList.get(j)) == 0) {
                            nonDuplicateOCRList.get(i).setK(nonDuplicateOCRList.get(i).getK() + 1);
                            if (nonDuplicateOCRList.get(i).getK() == 2) {
                                nonDuplicateOCRList.get(i).setCount(nonDuplicateOCRList.get(i).getCount() + 1);
                                break;
                            }
                        }
                    }
                }
            }

//            for (int j = 0; j < bigDecimalList.size(); j++) {
//                if (nonDuplicateOCRList.get(i).getBigDecimal().compareTo(bigDecimalList.get(j)) == 0) {
//                    nonDuplicateOCRList.get(i).setCount(nonDuplicateOCRList.get(i).getK() + 1);
//                    if (nonDuplicateOCRList.get(i).getK() == 2){
//                        nonDuplicateOCRList.get(i).setCount(nonDuplicateOCRList.get(i).getCount() + 1);
//                        break;
//                    }
//                }
//            }
//            result.add(nonDuplicateOCRList.get(i));
        } else {
            throw new RuntimeException("Invalid Bill");
        }
        return nonDuplicateOCRList;
    }


    public static void subsetSums(List<BigDecimal> bigDecimalList, int l,
                                  int r, BigDecimal sum,
                                  int count,
                                  HashSet<SubsetSum> sumHashSet) {
        // Print current subset
        if (l > r) {
            if (count > 1) {
                sumHashSet.add(new SubsetSum(sum));
            }
            return;
        }
        // Subset excluding arr[l]
        subsetSums(bigDecimalList, l + 1, r, sum, count, sumHashSet);
        count++;
        // Subset including arr[l]
        subsetSums(bigDecimalList, l + 1, r, sum.add(bigDecimalList.get(l)), count, sumHashSet);

    }

    public static List<BigDecimal> getListToCalculateSubsetSum(List<BigDecimal> bigDecimalList, List<OcrDTO> bigDecimaScoresList) {
        List<BigDecimal> result = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        if (bigDecimalList.size() != 0) {
            for (BigDecimal bigDecimal : bigDecimalList) {
                String str = bigDecimal.toBigInteger().toString();
                numberList.add(str.toCharArray().length);
            }

            BigDecimal mean = BigDecimal.ZERO;
            for (Integer integer : numberList) {
                mean = mean.add(new BigDecimal(integer));
            }
            mean = mean.divide(new BigDecimal(numberList.size()), RoundingMode.HALF_UP);

            List<StatisticNumber> statisticNumberList = new ArrayList<>();
            for (BigDecimal bigDecimal : bigDecimalList) {
                statisticNumberList.add(new StatisticNumber(bigDecimal, BigDecimal.ZERO));
            }

            for (int i = 0; i < statisticNumberList.size(); i++) {
                StatisticNumber statisticNumber = statisticNumberList.get(i);
                BigDecimal number = new BigDecimal(statisticNumber.getValue().toBigInteger());
                BigDecimal deviation = mean.subtract(number).abs();
                statisticNumber.setDeviation(deviation);
            }

//            for (OcrDTO ocrDTO : bigDecimaScoresList) {
//                for (StatisticNumber statisticNumber : statisticNumberList) {
//                    if (ocrDTO.getBigDecimal().compareTo(statisticNumber.getValue()) ==0){
//                        statisticNumber.setDeviation(BigDecimal.valueOf(ocrDTO.getCount()-statisticNumber.deviation.toBigInteger().intValue()));
//                    }
//                }
//            }

            statisticNumberList.sort((o1, o2) -> {
                return o2.deviation.compareTo(o1.deviation);
            });

            for (OcrDTO ocrDTO : bigDecimaScoresList) {
                for (int i = 0; i < statisticNumberList.size(); i++) {
                    if (result.size() >= 15) {
                        break;
                    }
                    if (ocrDTO.getBigDecimal().compareTo(statisticNumberList.get(i).getValue()) == 0) {
                        if (ocrDTO.getCount() >= 2) {
                            result.add(statisticNumberList.get(i).getValue());
                        }
                    }
                }
            }

//            for (int i = 0; i < 15; i++) {
//                if (i >= statisticNumberList.size()) {
//                    break;
//                }
//                result.add(statisticNumberList.get(i).getValue());
//            }
        } else {
            throw new RuntimeException("Invalid Bill");
        }
        return result;
    }

    @Data
    private static class StatisticNumber {
        private BigDecimal value;
        private BigDecimal deviation;
        private Integer number;

        public StatisticNumber(BigDecimal value, BigDecimal deviation) {
            this.value = value;
            this.deviation = deviation;
        }
    }
}
