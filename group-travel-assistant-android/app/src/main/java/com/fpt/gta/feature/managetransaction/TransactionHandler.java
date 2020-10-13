package com.fpt.gta.feature.managetransaction;

import com.fpt.gta.data.dto.BudgetOverViewDTO;
import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.TransactionDTO;

import static com.fpt.gta.data.dto.TransactionDTO.TransactionDetailDTO;

import com.fpt.gta.data.dto.constant.TransactionCategory;
import com.fpt.gta.data.dto.constant.TransactionDetailType;
import com.fpt.gta.data.dto.constant.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;

public final class TransactionHandler {

    private static final Map<Integer, BigDecimal> oneSideSign = Stream.of(new Object[][]{
            {TransactionDetailType.CASH, BigDecimal.valueOf(1)},
            {TransactionDetailType.EXPENSE, BigDecimal.valueOf(1)},
            {TransactionDetailType.INCOME, BigDecimal.valueOf(-1)},
            {TransactionDetailType.PAYABLE, BigDecimal.valueOf(-1)},
            {TransactionDetailType.RECEIVABLE, BigDecimal.valueOf(1)}
    }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (BigDecimal) data[1]));

    private static Boolean isValid(TransactionDTO transactionDTO) {
        List<TransactionDetailDTO> transactionDetailDTOList = transactionDTO.getTransactionDetailList();

        if (transactionDetailDTOList == null) {
            return false;
        }
        if (transactionDetailDTOList.size() == 0) {
            return false;
        }

        Map<Integer, BigDecimal> balanceMap = new HashMap<>();

        for (TransactionDetailDTO transactionDetailDTO : transactionDetailDTOList) {
            Integer idMember = transactionDetailDTO.getMember().getId();
            if (!balanceMap.containsKey(idMember)) {
                balanceMap.put(idMember, BigDecimal.valueOf(0));
            }
            BigDecimal oldValue = balanceMap.get(idMember);
            BigDecimal newValue = oldValue
                    .add(
                            transactionDetailDTO.getAmount()
                                    .multiply(
                                            oneSideSign.get(transactionDetailDTO.getIdType())
                                    )
                    );
            balanceMap.put(idMember, newValue);
        }

        for (Map.Entry<Integer, BigDecimal> integerBigDecimalEntry : balanceMap.entrySet()) {
            if (integerBigDecimalEntry.getValue().compareTo(BigDecimal.valueOf(0)) != 0) {
                return false;
            }
        }
        BigDecimal payerAmount = payerDetailOf(transactionDTO).getAmount().abs();

        BigDecimal totalParticipantAmount = BigDecimal.ZERO;
        for (TransactionDetailDTO transactionDetailDTO : participantListOf(transactionDTO)) {
            totalParticipantAmount = totalParticipantAmount.add(transactionDetailDTO.getAmount().abs());
        }
        totalParticipantAmount = totalParticipantAmount.add(groupShareDetailOf(transactionDTO).getAmount());
        if (payerAmount.compareTo(totalParticipantAmount) != 0) {
            return false;
        }

        return true;
    }

    public static void preparedTransactionDTO(TransactionDTO transactionDTO, TransactionDetailDTO payerDetailDTO) {
        if (transactionDTO.getIdType() == null) {
            throw new RuntimeException("need id type is 0 or null");
        }
        if (transactionDTO.getIdType() == 0) {
            throw new RuntimeException("need id type is 0 or null");
        }
//        if (transactionDTO.getTransactionDetailList().size() == 0) {
//            throw new RuntimeException("Transaction Detail is Empty");
//        }

        transactionDTO.getTransactionDetailList().removeIf(
                transactionDetailDTO -> transactionDetailDTO.getAmount().equals(BigDecimal.ZERO)
        );

//        BigDecimal sum = BigDecimal.ZERO;
//        for (TransactionDetailDTO detailDTO : transactionDTO.getTransactionDetailList()) {
//            sum = sum.add(detailDTO.getAmount());
//        }
//        if (sum.compareTo(payerDetailDTO.getAmount()) != 0) {
//            throw new RuntimeException("Invalid transaction");
//        }

        List<TransactionDetailDTO> oldTransactionDetailList = transactionDTO.getTransactionDetailList();
        transactionDTO.setOldTransactionDetailList(oldTransactionDetailList);

        List<TransactionDetailDTO> validTransactionDetailList = new ArrayList<>();

        Integer idType = transactionDTO.getIdType();
        switch (idType) {
            case TransactionType.EXPENSE:
//                prepare payer detail
                validTransactionDetailList.add(
                        new TransactionDetailDTO(
                                TransactionDetailType.CASH,
                                payerDetailDTO.getAmount().abs().negate(),
                                payerDetailDTO.getMember()));
                validTransactionDetailList.add(
                        new TransactionDetailDTO(
                                TransactionDetailType.RECEIVABLE,
                                payerDetailDTO.getAmount().abs(),
                                payerDetailDTO.getMember()));
//                prepare participant detail
                for (TransactionDetailDTO transactionDetailDTO : transactionDTO.getTransactionDetailList()) {
                    validTransactionDetailList.add
                            (new TransactionDetailDTO(
                                    TransactionDetailType.PAYABLE,
                                    transactionDetailDTO.getAmount().abs(),
                                    transactionDetailDTO.getMember()));
                    validTransactionDetailList.add(
                            new TransactionDetailDTO(
                                    TransactionDetailType.EXPENSE,
                                    transactionDetailDTO.getAmount().abs(),
                                    transactionDetailDTO.getMember()));
                }
                break;

            case TransactionType.TRANSFER:
//                prepare payer detail
                validTransactionDetailList.add(
                        new TransactionDetailDTO(TransactionDetailType.CASH,
                                payerDetailDTO.getAmount().abs().negate(),
                                payerDetailDTO.getMember()));
                validTransactionDetailList.add(
                        new TransactionDetailDTO(TransactionDetailType.RECEIVABLE,
                                payerDetailDTO.getAmount().abs(),
                                payerDetailDTO.getMember()));

//                prepare participant detail
                for (TransactionDetailDTO transactionDetailDTO : transactionDTO.getTransactionDetailList()) {
                    validTransactionDetailList.add
                            (new TransactionDetailDTO(
                                    TransactionDetailType.CASH,
                                    transactionDetailDTO.getAmount().abs(),
                                    transactionDetailDTO.getMember()));
                    validTransactionDetailList.add(
                            new TransactionDetailDTO(
                                    TransactionDetailType.PAYABLE,
                                    transactionDetailDTO.getAmount().abs(),
                                    transactionDetailDTO.getMember()));
                }
                break;

            case TransactionType.INCOME:
//                prepare payer detail
                validTransactionDetailList.add(
                        new TransactionDetailDTO(TransactionDetailType.CASH,
                                payerDetailDTO.getAmount().abs(),
                                payerDetailDTO.getMember()));
                validTransactionDetailList.add(
                        new TransactionDetailDTO(TransactionDetailType.PAYABLE,
                                payerDetailDTO.getAmount().abs(),
                                payerDetailDTO.getMember()));

//                prepare participant detail
                for (TransactionDetailDTO transactionDetailDTO : transactionDTO.getTransactionDetailList()) {
                    validTransactionDetailList.add
                            (new TransactionDetailDTO(
                                    TransactionDetailType.RECEIVABLE,
                                    transactionDetailDTO.getAmount().abs(),
                                    transactionDetailDTO.getMember()));
                    validTransactionDetailList.add(
                            new TransactionDetailDTO(
                                    TransactionDetailType.INCOME,
                                    transactionDetailDTO.getAmount().abs(),
                                    transactionDetailDTO.getMember()));
                }
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + idType);
        }

        transactionDTO.setTransactionDetailList(validTransactionDetailList);

        if (!isValid(transactionDTO)) {
            transactionDTO.setTransactionDetailList(transactionDTO.getOldTransactionDetailList());
            throw new RuntimeException("prepare error !!!");
        }
    }


    public static void preparedTransactionDTO(TransactionDTO transactionDTO,
                                              TransactionDetailDTO payerDetailDTO,
                                              TransactionDetailDTO groupShareDetailDTO) {
        transactionDTO.getTransactionDetailList().removeIf(transactionDetailDTO -> isGroupShareDetail(transactionDetailDTO));
        transactionDTO.getTransactionDetailList().add(new TransactionDetailDTO(0, groupShareDetailDTO.getAmount().abs(), groupShareDetailDTO.getMember()));
        preparedTransactionDTO(transactionDTO, payerDetailDTO);

//        if (groupShareDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
//            transactionDTO.getTransactionDetailList().add(new TransactionDetailDTO(
//                    TransactionDetailType.EXPENSE,
//                    groupShareDetailDTO.getAmount().abs(),
//                    groupShareDetailDTO.getMember()));
//            transactionDTO.getTransactionDetailList().add(new TransactionDetailDTO(
//                    TransactionDetailType.PAYABLE,
//                    groupShareDetailDTO.getAmount().abs(),
//                    groupShareDetailDTO.getMember()));
//        }

        BigDecimal sum = BigDecimal.ZERO;
        for (TransactionDetailDTO detailDTO : participantListOf(transactionDTO)) {
            sum = sum.add(detailDTO.getAmount().abs());
        }
        sum = sum.add(groupShareDetailDTO.getAmount().abs());

        if (sum.compareTo(payerDetailDTO.getAmount()) != 0) {
            transactionDTO.setTransactionDetailList(transactionDTO.getOldTransactionDetailList());
            throw new RuntimeException("Invalid transaction");
        }

        if (!isValid(transactionDTO)) {
            transactionDTO.setTransactionDetailList(transactionDTO.getOldTransactionDetailList());
            throw new RuntimeException("prepare error !!!");
        }
        transactionDTO.setOldTransactionDetailList(null);
    }

    public static TransactionDetailDTO groupShareDetailOf(TransactionDTO transactionDTO) {
        for (TransactionDetailDTO transactionDetailDTO : transactionDTO.getTransactionDetailList()) {
            if (isGroupShareDetail(transactionDetailDTO)) {
                return new TransactionDetailDTO(0, transactionDetailDTO.getAmount(), transactionDetailDTO.getMember());
            }
        }
        return new TransactionDetailDTO();
    }

    public static boolean isGroupShareDetail(TransactionDetailDTO transactionDetailDTO) {
        if (transactionDetailDTO.getMember() == null) {
            return true;
        }
        if (transactionDetailDTO.getMember().getId() == 0) {
            return true;
        }
        return false;
    }

    public static TransactionDetailDTO payerDetailOf(TransactionDTO transactionDTO) {

        int idType = transactionDTO.getIdType();
        switch (idType) {
            case TransactionType.EXPENSE:
            case TransactionType.TRANSFER:
                for (TransactionDetailDTO transactionDetailDTO : transactionDTO.getTransactionDetailList()) {
                    if (transactionDetailDTO.getIdType().equals(TransactionDetailType.CASH)) {
                        if (transactionDetailDTO.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                            return new TransactionDetailDTO(transactionDetailDTO.getAmount().abs(), transactionDetailDTO.getMember());
                        }
                    }
                }
                break;

            case TransactionType.INCOME:
                for (TransactionDetailDTO transactionDetailDTO : transactionDTO.getTransactionDetailList()) {
                    if (transactionDetailDTO.getIdType().equals(TransactionDetailType.CASH)) {
                        if (transactionDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                            return new TransactionDetailDTO(transactionDetailDTO.getAmount().abs(), transactionDetailDTO.getMember());
                        }
                    }
                }
                break;

            default:
                throw new IllegalStateException("TransactionType: " + idType);
        }
        throw new RuntimeException("get value error !!!");
    }

    public static List<TransactionDetailDTO> participantListOf(TransactionDTO transactionDTO) {
        Integer idType = transactionDTO.getIdType();

        List<TransactionDetailDTO> transactionDetailWithOutGroupShare = new ArrayList<>();

        for (TransactionDetailDTO transactionDetailDTO : transactionDTO.getTransactionDetailList()) {
            if (!isGroupShareDetail(transactionDetailDTO)) {
                transactionDetailWithOutGroupShare.add(transactionDetailDTO);
            }
        }

        List<TransactionDetailDTO> result = new ArrayList<>();
        switch (idType) {
            case TransactionType.EXPENSE:
            case TransactionType.TRANSFER:
                for (TransactionDetailDTO transactionDetailDTO : transactionDetailWithOutGroupShare) {
                    if (transactionDetailDTO.getIdType().equals(TransactionDetailType.PAYABLE)) {
                        result.add(new TransactionDetailDTO(transactionDetailDTO.getAmount().abs(), transactionDetailDTO.getMember()));
                    }
                }
                break;
            case TransactionType.INCOME:
                for (TransactionDetailDTO transactionDetailDTO : transactionDetailWithOutGroupShare) {
                    if (transactionDetailDTO.getIdType().equals(TransactionDetailType.RECEIVABLE)) {
                        result.add(new TransactionDetailDTO(transactionDetailDTO.getAmount().abs(), transactionDetailDTO.getMember()));
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + idType);
        }
        return result;
    }

    public static List<TransactionDetailDTO> balanceOf(List<TransactionDTO> transactionDTOList) {
        Map<Integer, TransactionDetailDTO> balanceMap = new HashMap<>();

        for (TransactionDTO transactionDTO : getDividedTransactionList(transactionDTOList)) {
//        for (TransactionDTO transactionDTO : (transactionDTOList)) {
            BigDecimal rate = BigDecimal.ONE;
            BigDecimal customCurrencyRate = transactionDTO.getCustomCurrencyRate();
            BigDecimal defaultCurrencyRate = transactionDTO.getDefaultCurrencyRate();
            if (customCurrencyRate.compareTo(BigDecimal.ZERO) != 0) {
                rate = customCurrencyRate;
            } else {
                rate = defaultCurrencyRate;
            }

            List<TransactionDetailDTO> transactionDetailDTOList = transactionDTO.getTransactionDetailList();
            if (transactionDetailDTOList == null) {
                throw new RuntimeException("detail list is null");
            }
            if (transactionDetailDTOList.size() == 0) {
                throw new RuntimeException("detail list is empty");
            }
            Map<Integer, TransactionDetailDTO> transactionInnerMap = new HashMap<>();

            //process amount
            for (TransactionDetailDTO transactionDetailDTO : transactionDetailDTOList) {
                Integer idType = transactionDetailDTO.getIdType();
                MemberDTO memberDTO = transactionDetailDTO.getMember();
                Integer idMember = memberDTO.getId();
                BigDecimal newAmount = transactionDetailDTO.getAmount();
//                        .multiply(rate)
//                        .setScale(2, RoundingMode.DOWN);

                balanceMap.putIfAbsent(idMember, new TransactionDetailDTO(0, BigDecimal.ZERO, memberDTO));
                transactionInnerMap.putIfAbsent(idMember, new TransactionDetailDTO(0, BigDecimal.ZERO, memberDTO));
                TransactionDetailDTO oldBalanceDetailDTO = balanceMap.get(idMember);
                TransactionDetailDTO oldInnerBalanceDetailDTO = transactionInnerMap.get(idMember);

                if (idType.equals(TransactionDetailType.RECEIVABLE)) {

                    BigDecimal oldBalanceAmount = oldBalanceDetailDTO.getAmount();
                    BigDecimal newBalanceAmount = oldBalanceAmount.add(newAmount);
                    oldBalanceDetailDTO.setAmount(newBalanceAmount);

                    BigDecimal oldInnerBalanceAmount = oldInnerBalanceDetailDTO.getAmount();
                    BigDecimal newInnerBalanceAmount = oldInnerBalanceAmount.add(newAmount);
                    oldInnerBalanceDetailDTO.setAmount(newInnerBalanceAmount);

                } else if (idType.equals(TransactionDetailType.PAYABLE)) {

                    BigDecimal oldBalanceAmount = oldBalanceDetailDTO.getAmount();
                    BigDecimal newBalanceAmount = oldBalanceAmount.subtract(newAmount);
                    oldBalanceDetailDTO.setAmount(newBalanceAmount);

                    BigDecimal oldInnerBalanceAmount = oldInnerBalanceDetailDTO.getAmount();
                    BigDecimal newInnerBalanceAmount = oldInnerBalanceAmount.subtract(newAmount);
                    oldInnerBalanceDetailDTO.setAmount(newInnerBalanceAmount);
                }
            }

            // kiem tra sai so
            BigDecimal innerAmount = BigDecimal.ZERO;
            for (TransactionDetailDTO transactionDetailDTO : transactionInnerMap.values()) {
                innerAmount = innerAmount.add(transactionDetailDTO.getAmount());
            }
            switch (innerAmount.compareTo(BigDecimal.ZERO)) {
                case 0:
                    break;
                case 1:
                case -1:
                    Integer payerId = payerDetailOf(transactionDTO).getMember().getId();
                    TransactionDetailDTO oldBalanceDetailDTO = balanceMap.get(payerId);
                    BigDecimal oldBalanceAmount = oldBalanceDetailDTO.getAmount();
                    BigDecimal newBalanceAmount = oldBalanceAmount.subtract(innerAmount);
                    oldBalanceDetailDTO.setAmount(newBalanceAmount);
                    break;
            }
        }

        List<TransactionDetailDTO> result = new ArrayList<>();

        for (Map.Entry<Integer, TransactionDetailDTO> integerTransactionDetailDTOEntry : balanceMap.entrySet()) {
            if (integerTransactionDetailDTOEntry.getValue().getAmount().compareTo(BigDecimal.ZERO) != 0) {
                result.add(integerTransactionDetailDTOEntry.getValue());
            }
        }
        result.sort((o1, o2) -> {
            return o2.getAmount().abs().compareTo(o1.getAmount().abs());
        });
        return result;
    }

    public static BigDecimal myExpenseOf(List<TransactionDTO> transactionDTOList, String firebaseUid) {
        BigDecimal expense = BigDecimal.ZERO;
        for (TransactionDTO transactionDTO : transactionDTOList) {

            BigDecimal rate = BigDecimal.ONE;
            BigDecimal customCurrencyRate = transactionDTO.getCustomCurrencyRate();
            BigDecimal defaultCurrencyRate = transactionDTO.getDefaultCurrencyRate();
            if (customCurrencyRate.compareTo(BigDecimal.ZERO) != 0) {
                rate = customCurrencyRate;
            } else {
                rate = defaultCurrencyRate;
            }

            List<TransactionDetailDTO> transactionDetailDTOList = transactionDTO.getTransactionDetailList();
            if (transactionDetailDTOList == null) {
                throw new RuntimeException("detail list is null");
            }
            if (transactionDetailDTOList.size() == 0) {
                throw new RuntimeException("detail list is empty");
            }

            for (TransactionDetailDTO transactionDetailDTO : transactionDetailDTOList) {
                Integer idType = transactionDetailDTO.getIdType();
                MemberDTO memberDTO = transactionDetailDTO.getMember();
                Integer idMember = memberDTO.getId();
                BigDecimal newAmount = transactionDetailDTO.getAmount()
                        .multiply(rate).setScale(2, RoundingMode.DOWN);

                if (memberDTO.getPerson().getFirebaseUid().equals(firebaseUid)) {
                    if (idType.equals(TransactionDetailType.EXPENSE)) {
                        expense = expense.add(newAmount);
                    } else if (idType.equals(TransactionDetailType.INCOME)) {
                        expense = expense.subtract(newAmount);
                    }
                }
            }
        }
        return expense;
    }

    public static BigDecimal myCashOf(List<TransactionDTO> transactionDTOList, String firebaseUid) {
        BigDecimal cash = BigDecimal.ZERO;
        for (TransactionDTO transactionDTO : transactionDTOList) {
            BigDecimal rate = BigDecimal.ONE;
            BigDecimal customCurrencyRate = transactionDTO.getCustomCurrencyRate();
            BigDecimal defaultCurrencyRate = transactionDTO.getDefaultCurrencyRate();
            if (customCurrencyRate.compareTo(BigDecimal.ZERO) != 0) {
                rate = customCurrencyRate;
            } else {
                rate = defaultCurrencyRate;
            }

            List<TransactionDetailDTO> transactionDetailDTOList = transactionDTO.getTransactionDetailList();
            if (transactionDetailDTOList == null) {
                throw new RuntimeException("detail list is null");
            }
            if (transactionDetailDTOList.size() == 0) {
                throw new RuntimeException("detail list is empty");
            }

            for (TransactionDetailDTO transactionDetailDTO : transactionDetailDTOList) {
                Integer idType = transactionDetailDTO.getIdType();
                MemberDTO memberDTO = transactionDetailDTO.getMember();
                Integer idMember = memberDTO.getId();
                BigDecimal newAmount = transactionDetailDTO.getAmount()
                        .multiply(rate).setScale(2, RoundingMode.DOWN);

                if (memberDTO.getPerson().getFirebaseUid().equals(firebaseUid)) {
                    if (idType.equals(TransactionDetailType.CASH)) {
                        cash = cash.add(newAmount);
                    }
                }
            }
        }
        return cash;
    }

    public static List<TransactionDTO> getPaybackFlow(List<TransactionDTO> transactionDTOList) {
        List<TransactionDTO> result = new ArrayList<>();
        //prepare transaction list
        List<TransactionDetailDTO> balanceList = balanceOf(transactionDTOList);
        List<TransactionDetailDTO> payableList = new ArrayList<>();
        List<TransactionDetailDTO> receivableList = new ArrayList<>();


        for (TransactionDetailDTO transactionDetail : balanceList) {
            if (transactionDetail.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                transactionDetail.setAmount(transactionDetail.getAmount().abs());
                receivableList.add(transactionDetail);
            } else {
                transactionDetail.setAmount(transactionDetail.getAmount().abs());
                payableList.add(transactionDetail);
            }
        }
        payableList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
        receivableList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));

        if (payableList.size() <= 15 && receivableList.size() <= 15) {
            //start algorithm
            List<SubsetSum> payableSubsetSumList = new ArrayList<>();
            List<SubsetSum> receivableSubsetSumList = new ArrayList<>();
            IntStream.of(1, 2).parallel().forEach(flag -> {
                if (flag == 1) {
                    processSubsetSumsRecursive(payableList, 0, BigDecimal.ZERO, new ArrayList<>(), payableSubsetSumList);
                    payableSubsetSumList.sort((o1, o2) -> o2.sum.compareTo(o1.sum));
                } else if (flag == 2) {
                    processSubsetSumsRecursive(receivableList, 0, BigDecimal.ZERO, new ArrayList<>(), receivableSubsetSumList);
                    receivableSubsetSumList.sort((o1, o2) -> o2.sum.compareTo(o1.sum));
                }
            });
            while (payableSubsetSumList.size() != 0 && receivableSubsetSumList.size() != 0) {
                SubsetSum payableSubsetSum = payableSubsetSumList.get(payableSubsetSumList.size() - 1);
                SubsetSum receivableSubsetSum = receivableSubsetSumList.get(receivableSubsetSumList.size() - 1);

                int c = payableSubsetSum.getSum().compareTo(receivableSubsetSum.getSum());
                if (c == 0) {
                    processPackingAlgorithm(result, payableSubsetSum.getTransactionDetailDTOList(), receivableSubsetSum.getTransactionDetailDTOList());

                    Map<Integer, MemberDTO> payedMemberMap = new HashMap<>();
                    Map<Integer, MemberDTO> receivedMemberMap = new HashMap<>();

                    for (TransactionDetailDTO transactionDetailDTO : payableSubsetSumList.get(payableSubsetSumList.size() - 1).getTransactionDetailDTOList()) {
                        payedMemberMap.put(transactionDetailDTO.getMember().getId(), transactionDetailDTO.getMember());
                    }
                    for (TransactionDetailDTO transactionDetailDTO : receivableSubsetSumList.get(receivableSubsetSumList.size() - 1).getTransactionDetailDTOList()) {
                        receivedMemberMap.put(transactionDetailDTO.getMember().getId(), transactionDetailDTO.getMember());
                    }

                    payableSubsetSumList.remove(payableSubsetSumList.size() - 1);
                    receivableSubsetSumList.remove(receivableSubsetSumList.size() - 1);

                    //remove invalid subset sum
//                    IntStream.of(1, 2).parallel().forEach(flag -> {
//                        if (flag == 1) {
//                            for (int i = payableSubsetSumList.size() - 1; i >= 0; i--) {
//                                SubsetSum subsetSum = payableSubsetSumList.get(payableSubsetSumList.size() - 1);
//                                boolean isDelete = false;
//                                for (TransactionDetailDTO transactionDetailDTO : subsetSum.getTransactionDetailDTOList()) {
//                                    if (payedMemberMap.get(transactionDetailDTO.getMember().getId()) == null) {
//                                        isDelete = true;
//                                        break;
//                                    }
//                                }
//                                if (isDelete) {
//                                    payableSubsetSumList.remove(i);
//                                }
//                            }
//                        } else if (flag == 2) {
//                            for (int i = receivableSubsetSumList.size() - 1; i >= 0; i--) {
//                                SubsetSum subsetSum = receivableSubsetSumList.get(receivableSubsetSumList.size() - 1);
//                                boolean isDelete = false;
//                                for (TransactionDetailDTO transactionDetailDTO : subsetSum.getTransactionDetailDTOList()) {
//                                    if (receivedMemberMap.get(transactionDetailDTO.getMember().getId()) == null) {
//                                        isDelete = true;
//                                        break;
//                                    }
//                                }
//                                if (isDelete) {
//                                    receivableSubsetSumList.remove(i);
//                                }
//                            }
//                        }
//                    });

                } else if (c > 0) {
                    receivableSubsetSumList.remove(receivableSubsetSumList.size() - 1);
                } else {
                    payableSubsetSumList.remove(payableSubsetSumList.size() - 1);
                }
            }
            //end algorithm
        }
        processPackingAlgorithm(result, payableList, receivableList);

        return result;
    }

    private static void processPackingAlgorithm(List<TransactionDTO> result, List<TransactionDetailDTO> payableList, List<TransactionDetailDTO> receivableList) {
        organizeBalance(payableList, receivableList);
//        BigDecimal totalPayable = BigDecimal.ZERO;
//        BigDecimal totalReceivable = BigDecimal.ZERO;
//        for (TransactionDetailDTO transactionDetailDTO : payableList) {
//            totalPayable = totalPayable.add(transactionDetailDTO.getAmount());
//        }
//        for (TransactionDetailDTO transactionDetailDTO : receivableList) {
//            totalReceivable = totalReceivable.add(transactionDetailDTO.getAmount());
//        }

        while (payableList.size() != 0 && receivableList.size() != 0) {
            TransactionDetailDTO highestPayableDetailDTO = payableList.get(0);
            TransactionDetailDTO highestReceivableDetailDTO = receivableList.get(0);

            //get pay amount
            BigDecimal highestPayableAmount = highestPayableDetailDTO.getAmount();
            BigDecimal highestReceivableAmount = highestReceivableDetailDTO.getAmount();
            BigDecimal payAmount = highestPayableAmount.compareTo(highestReceivableAmount) < 0 ? highestPayableAmount : highestReceivableAmount;

            //create transfer transaction
            TransactionDTO paybackTransactionDTO = new TransactionDTO();
            prepareTransferTransactionForPaybackFlow(highestPayableDetailDTO, highestReceivableDetailDTO, payAmount, paybackTransactionDTO);
            //add to result
            result.add(paybackTransactionDTO);
            organizeBalance(payableList, receivableList);
        }
    }

    private static void organizeBalance(List<TransactionDetailDTO> payableList, List<TransactionDetailDTO> receivableList) {
        //remove zero balance
        payableList.removeIf(transactionDetailDTO -> transactionDetailDTO.getAmount().compareTo(BigDecimal.ZERO) == 0);
        receivableList.removeIf(transactionDetailDTO -> transactionDetailDTO.getAmount().compareTo(BigDecimal.ZERO) == 0);
        //sort again
        payableList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
        receivableList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
    }

    private static void prepareTransferTransactionForPaybackFlow(TransactionDetailDTO highestPayableDetailDTO, TransactionDetailDTO highestReceivableDetailDTO, BigDecimal payAmount, TransactionDTO tempTransactionDTO) {
        tempTransactionDTO.setIdType(TransactionType.TRANSFER);
        tempTransactionDTO.setName("Reimbursement");

        TransactionDetailDTO payerDetailDTO = new TransactionDetailDTO();
        payerDetailDTO.setAmount(payAmount);
        payerDetailDTO.setMember(highestPayableDetailDTO.getMember());

        TransactionDetailDTO participantDetailDTO = new TransactionDetailDTO();
        participantDetailDTO.setAmount(payAmount);
        participantDetailDTO.setMember(highestReceivableDetailDTO.getMember());
        tempTransactionDTO.setTransactionDetailList(Arrays.asList(participantDetailDTO));
        preparedTransactionDTO(tempTransactionDTO, payerDetailDTO);
        //End create transfer transaction

        //adjust amount
        highestPayableDetailDTO.setAmount(highestPayableDetailDTO.getAmount().subtract(payAmount));
        highestReceivableDetailDTO.setAmount(highestReceivableDetailDTO.getAmount().subtract(payAmount));
    }

    static void processSubsetSumsRecursive(List<TransactionDetailDTO> transactionDetailList,
                                           int l,
                                           BigDecimal sum,
                                           List<TransactionDetailDTO> callerList,
                                           List<SubsetSum> subsetSumList
    ) {
        // Process current subset
        List<TransactionDetailDTO> localList = new ArrayList<>();
        localList.addAll(callerList);
        if (l > transactionDetailList.size() - 1) {
            subsetSumList.add(new SubsetSum(sum, localList));
            return;
        }
        // Subset excluding
        processSubsetSumsRecursive(transactionDetailList, l + 1, sum, localList, subsetSumList);

        localList.add(transactionDetailList.get(l));

        // Subset including
        processSubsetSumsRecursive(transactionDetailList, l + 1, sum.add(transactionDetailList.get(l).getAmount()), localList, subsetSumList);

    }

    public static List<TransactionDTO> getPersonalTransactionList(List<TransactionDTO> mTransactionDTOList, String userId) {
        List<TransactionDTO> personalTransactionList = new ArrayList<>();
        if (mTransactionDTOList != null) {
            for (TransactionDTO transactionDTO : mTransactionDTOList) {
                List<TransactionDetailDTO> participantListOf = TransactionHandler.participantListOf(transactionDTO);
                TransactionDetailDTO payerTransactionDTO = TransactionHandler.payerDetailOf(transactionDTO);
                for (int i = 0; i < participantListOf.size(); i++) {
                    if (participantListOf.get(i).getMember().getPerson().getFirebaseUid().equals(userId)) {
                        personalTransactionList.add(transactionDTO);
                        break;
                    } else if (payerTransactionDTO.getMember().getPerson().getFirebaseUid().equals(userId)) {
                        personalTransactionList.add(transactionDTO);
                        break;
                    } else if (transactionDTO.getOwner().getPerson().getFirebaseUid().equals(userId)) {
                        personalTransactionList.add(transactionDTO);
                        break;
                    }
                }
            }
        }
        return personalTransactionList;
    }

    public static List<TransactionDTO> getGroupTransaction(List<TransactionDTO> transactionDTOList) {
        List<TransactionDTO> groupTransactionList = new ArrayList<>();
        if (transactionDTOList != null) {
            for (TransactionDTO dto : transactionDTOList) {
                TransactionDetailDTO groupShareDetailsDTO = new TransactionDetailDTO();
                groupShareDetailsDTO = TransactionHandler.groupShareDetailOf(dto);
                if (groupShareDetailsDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    groupTransactionList.add(dto);
                }
            }
        }
        return groupTransactionList;
    }

    public static List<TransactionDTO> getIndividualTransaction(List<TransactionDTO> transactionDTOList, String firebaseUid) {
        List<TransactionDTO> individualList = new ArrayList<>();
        if (transactionDTOList != null) {
            for (TransactionDTO transactionDTO : transactionDTOList) {
                TransactionDetailDTO groupShareDetailsDTO = TransactionHandler.groupShareDetailOf(transactionDTO);
                List<TransactionDetailDTO> participantList = TransactionHandler.participantListOf(transactionDTO);
                TransactionDetailDTO payerDetails = TransactionHandler.payerDetailOf(transactionDTO);
                for (TransactionDetailDTO transactionDetailDTO : participantList) {
                    if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid) && transactionDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        individualList.add(transactionDTO);
                    }
                }

                if (transactionDTO.getIdType().equals(TransactionType.TRANSFER)) {
                    if (payerDetails.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                        individualList.add(transactionDTO);
                    } else {
                        for (TransactionDetailDTO transactionDetailDTO : participantList) {
                            if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                                individualList.add(transactionDTO);
                            }
                        }
                    }
                } else if (transactionDTO.getIdType().equals(TransactionType.INCOME)) {
                    if (payerDetails.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                        individualList.add(transactionDTO);
                    } else {
                        for (TransactionDetailDTO transactionDetailDTO : participantList) {
                            if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                                individualList.add(transactionDTO);
                            }
                        }
                    }
                }
            }
        }
        return individualList;
    }


    public static List<TransactionDTO> getTransactionRelatedMe(List<TransactionDTO> transactionDTOList, String firebaseUid) {
        List<TransactionDTO> relatedMeTransactionList = new ArrayList<>();
        if (transactionDTOList != null) {
            for (TransactionDTO transactionDTO : transactionDTOList) {
                TransactionDetailDTO payerDetails = TransactionHandler.payerDetailOf(transactionDTO);
                TransactionDetailDTO groupShareDetailsDTO = TransactionHandler.groupShareDetailOf(transactionDTO);
                List<TransactionDetailDTO> participantList = TransactionHandler.participantListOf(transactionDTO);
                if (groupShareDetailsDTO.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                    for (TransactionDetailDTO transactionDetailDTO : participantList) {
                        if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid) && transactionDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                            relatedMeTransactionList.add(transactionDTO);
                        }
                    }
                } else if (groupShareDetailsDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    relatedMeTransactionList.add(transactionDTO);
                }

                if (transactionDTO.getIdType().equals(TransactionType.TRANSFER)) {
                    if (payerDetails.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                        relatedMeTransactionList.add(transactionDTO);
                    } else {
                        for (TransactionDetailDTO transactionDetailDTO : participantList) {
                            if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                                relatedMeTransactionList.add(transactionDTO);
                            }
                        }
                    }
                } else if (transactionDTO.getIdType().equals(TransactionType.INCOME)) {
                    if (payerDetails.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                        relatedMeTransactionList.add(transactionDTO);
                    } else {
                        for (TransactionDetailDTO transactionDetailDTO : participantList) {
                            if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                                relatedMeTransactionList.add(transactionDTO);
                            }
                        }
                    }
                }
            }
        }
        return relatedMeTransactionList;
    }

    public static TransactionDTO getDividedTransaction(TransactionDTO transactionDTO) {
        //prepare rate
        BigDecimal rate = BigDecimal.ONE;
        BigDecimal customCurrencyRate = transactionDTO.getCustomCurrencyRate();
        BigDecimal defaultCurrencyRate = transactionDTO.getDefaultCurrencyRate();
        if (customCurrencyRate.compareTo(BigDecimal.ZERO) != 0) {
            rate = customCurrencyRate;
        } else {
            rate = defaultCurrencyRate;
        }

        //prepare
        TransactionDetailDTO payerDetailDTO = payerDetailOf(transactionDTO);
        TransactionDetailDTO groupShareDetailDTO = groupShareDetailOf(transactionDTO);
        List<TransactionDetailDTO> participantList = participantListOf(transactionDTO);

        //setup participant map
        Map<Integer, BigDecimal> participantMap = new HashMap<>();
        for (TransactionDetailDTO transactionDetailDTO : participantList) {
            Integer idMember = transactionDetailDTO.getMember().getId();
            BigDecimal oldValue = participantMap.getOrDefault(idMember, BigDecimal.ZERO);
            participantMap.put(idMember, oldValue.add(transactionDetailDTO.getAmount()));
        }

        //chia tien group share
        BigDecimal memberSize = BigDecimal.valueOf(transactionDTO.getMemberList().size());
        BigDecimal dividedAmount = groupShareDetailDTO.getAmount().divide(memberSize, RoundingMode.DOWN).setScale(2, RoundingMode.DOWN);
        //share the group share value to all members
        for (MemberDTO memberDTO : transactionDTO.getMemberList()) {
            Integer idMember = memberDTO.getId();
            BigDecimal oldValue = participantMap.getOrDefault(idMember, BigDecimal.ZERO);
            participantMap.put(idMember, oldValue.add(dividedAmount));
        }
        //change the participantList
        ArrayList tempParticipantList = new ArrayList();
        for (MemberDTO memberDTO : transactionDTO.getMemberList()) {
            BigDecimal latestValue = participantMap.get(memberDTO.getId());
            if (latestValue.compareTo(BigDecimal.ZERO) > 0) {
                tempParticipantList.add(new TransactionDetailDTO(latestValue, memberDTO));

            }
        }
        participantList = tempParticipantList;

        //fix sai so cho payer
        BigDecimal error = dividedAmount.multiply(memberSize).subtract(groupShareDetailDTO.getAmount());
        payerDetailDTO.setAmount(payerDetailDTO.getAmount().add(error));


        //convert rate and get total
        BigDecimal total = BigDecimal.ZERO;
//chia roi ko cong nua =))
//        groupShareDetailDTO.setAmount(groupShareDetailDTO.getAmount().multiply(rate).setScale(2, RoundingMode.DOWN));
//        total = total.add(groupShareDetailDTO.getAmount());

        for (TransactionDetailDTO participantDetailDTO : participantList) {
            participantDetailDTO.setAmount(participantDetailDTO.getAmount().multiply(rate).setScale(2, RoundingMode.DOWN));
            total = total.add(participantDetailDTO.getAmount());
        }

        //set total to payer
        payerDetailDTO.setAmount(total);

        // prepare transaction
        TransactionDTO convertedTransactionDTO = new TransactionDTO();
        convertedTransactionDTO.setId(transactionDTO.getId());
        convertedTransactionDTO.setName(transactionDTO.getName());
        convertedTransactionDTO.setIdType(transactionDTO.getIdType());
        convertedTransactionDTO.setOccurAt(transactionDTO.getOccurAt());
        convertedTransactionDTO.setOwner(transactionDTO.getOwner());
        convertedTransactionDTO.setIdCategory(transactionDTO.getIdCategory());
        convertedTransactionDTO.setDocumentList(transactionDTO.getDocumentList());
        convertedTransactionDTO.setCurrency(transactionDTO.getCurrency());
        convertedTransactionDTO.setCustomCurrencyRate(transactionDTO.getCustomCurrencyRate());
        convertedTransactionDTO.setDefaultCurrencyRate(transactionDTO.getDefaultCurrencyRate());
        convertedTransactionDTO.setOccurTrip(transactionDTO.getOccurTrip());

        convertedTransactionDTO.setTransactionDetailList(participantList);

        preparedTransactionDTO(convertedTransactionDTO, payerDetailDTO);

        return convertedTransactionDTO;
    }

    public static List<TransactionDTO> getDividedTransactionList
            (List<TransactionDTO> transactionDTOList) {
        List<TransactionDTO> resultList = new ArrayList<>();

        for (TransactionDTO transactionDTO : transactionDTOList) {
            resultList.add(getDividedTransaction(transactionDTO));
        }

        return resultList;
    }

    public static List<TransactionDTO> getIndividualTransactionList(List<TransactionDTO> transactionDTOList, String firebaseUid) {
        List<TransactionDTO> individualTransactionList = new ArrayList<>();

        if (transactionDTOList != null) {
            for (TransactionDTO dto : transactionDTOList) {
                TransactionDetailDTO groupShareDetailsDTO = TransactionHandler.groupShareDetailOf(dto);
                List<TransactionDetailDTO> participantListOf = TransactionHandler.participantListOf(dto);
                if (groupShareDetailsDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    individualTransactionList.add(dto);
                } else if (groupShareDetailsDTO.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                    for (TransactionDetailDTO transactionDetailDTO : participantListOf) {
                        if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                            individualTransactionList.add(dto);
                        }
                    }
                }
            }
        }

        return individualTransactionList;
    }

    public static boolean checkExits
            (List<TransactionDTO.TransactionDetailDTO> participantList, String firebaseUid) {
        for (TransactionDTO.TransactionDetailDTO transactionDetailDTO : participantList) {
            if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                return true;
            }
        }
        return false;
    }

//    public static BudgetOverViewDTO getIndividualDetailsOfTransaction
//            (List<TransactionDTO> transactionDTOList, String firebaseUid) {
//        BudgetOverViewDTO budgetOverViewDTO = new BudgetOverViewDTO();
//        BigDecimal actualCost = BigDecimal.ZERO;
//        BigDecimal activityCost = BigDecimal.ZERO;
//        BigDecimal accommodationCost = BigDecimal.ZERO;
//        BigDecimal transportationCost = BigDecimal.ZERO;
//        BigDecimal foodCost = BigDecimal.ZERO;
//
//        if (transactionDTOList != null) {
//            for (TransactionDTO transactionDTO : getDividedTransactionList(transactionDTOList)) {
//                BigDecimal myExpenseInTransaction = BigDecimal.ZERO;
//                for (TransactionDetailDTO transactionDetailDTO : transactionDTO.getTransactionDetailList()) {
//                    if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
//                        if (transactionDetailDTO.getIdType().equals(TransactionDetailType.EXPENSE)) {
//                            myExpenseInTransaction = myExpenseInTransaction.add(transactionDetailDTO.getAmount().abs());
//                        } else if (transactionDetailDTO.getIdType().equals(TransactionDetailType.INCOME)) {
//                            myExpenseInTransaction = myExpenseInTransaction.subtract(transactionDetailDTO.getAmount().abs());
//                        }
//                    }
//                }
//                actualCost = actualCost.add(myExpenseInTransaction);
//                if (transactionDTO.getIdCategory().equals(TransactionCategory.ACTIVITY)) {
//                    activityCost = activityCost.add(myExpenseInTransaction);
//                } else if (transactionDTO.getIdCategory().equals(TransactionCategory.ACCOMMODATION)) {
//                    accommodationCost = accommodationCost.add(myExpenseInTransaction);
//                } else if (transactionDTO.getIdCategory().equals(TransactionCategory.TRANSPORTATION)) {
//                    transportationCost = transportationCost.add(myExpenseInTransaction);
//                } else if (transactionDTO.getIdCategory().equals(TransactionCategory.FOOD)) {
//                    foodCost = foodCost.add(myExpenseInTransaction);
//                }
//            }
//        }
//        budgetOverViewDTO.setActivityBudget(activityCost);
//        budgetOverViewDTO.setAccommodationBudget(accommodationCost);
//        budgetOverViewDTO.setTransportationBudget(transportationCost);
//        budgetOverViewDTO.setFoodBudget(foodCost);
//        budgetOverViewDTO.setActualCost(actualCost);
//        return budgetOverViewDTO;
//    }

    public static BudgetOverViewDTO getMyIndividualActual
            (List<TransactionDTO> transactionDTOList, String firebaseUid) {
        BudgetOverViewDTO budgetOverViewDTO = new BudgetOverViewDTO();
        BigDecimal actualCost = BigDecimal.ZERO;
        BigDecimal activityCost = BigDecimal.ZERO;
        BigDecimal accommodationCost = BigDecimal.ZERO;
        BigDecimal transportationCost = BigDecimal.ZERO;
        BigDecimal foodCost = BigDecimal.ZERO;

        if (transactionDTOList != null) {
            for (TransactionDTO transactionDTO : transactionDTOList) {
                if (transactionDTO.getIdType().equals(TransactionType.EXPENSE)) {
                    BigDecimal myExpenseInTransaction = BigDecimal.ZERO;
                    List<TransactionDTO.TransactionDetailDTO> participantList = TransactionHandler.participantListOf(transactionDTO);
                    for (TransactionDetailDTO transactionDetailDTO : participantList) {
                        if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                            myExpenseInTransaction = myExpenseInTransaction.add(transactionDetailDTO.getAmount().abs());
                        }
                    }
                    actualCost = actualCost.add(myExpenseInTransaction);
                    if (transactionDTO.getIdCategory().equals(TransactionCategory.ACTIVITY)) {
                        activityCost = activityCost.add(myExpenseInTransaction);
                    } else if (transactionDTO.getIdCategory().equals(TransactionCategory.ACCOMMODATION)) {
                        accommodationCost = accommodationCost.add(myExpenseInTransaction);
                    } else if (transactionDTO.getIdCategory().equals(TransactionCategory.TRANSPORTATION)) {
                        transportationCost = transportationCost.add(myExpenseInTransaction);
                    } else if (transactionDTO.getIdCategory().equals(TransactionCategory.FOOD)) {
                        foodCost = foodCost.add(myExpenseInTransaction);
                    }
                }
            }
        }
        budgetOverViewDTO.setActivityBudget(activityCost);
        budgetOverViewDTO.setAccommodationBudget(accommodationCost);
        budgetOverViewDTO.setTransportationBudget(transportationCost);
        budgetOverViewDTO.setFoodBudget(foodCost);
        budgetOverViewDTO.setActualCost(actualCost);
        return budgetOverViewDTO;
    }

    public static BudgetOverViewDTO getGroupDetailsOfTransaction(List<TransactionDTO> transactionDTOList) {
        BudgetOverViewDTO budgetOverViewDTO = new BudgetOverViewDTO();
        BigDecimal actualCost = BigDecimal.ZERO;
        BigDecimal activityCost = BigDecimal.ZERO;
        BigDecimal accommodationCost = BigDecimal.ZERO;
        BigDecimal transportationCost = BigDecimal.ZERO;
        BigDecimal foodCost = BigDecimal.ZERO;

        if (transactionDTOList != null) {
            for (TransactionDTO dto : transactionDTOList) {
                if (dto.getIdType().equals(TransactionType.EXPENSE)) {
                    TransactionDetailDTO groupShareDetailsDTO = TransactionHandler.groupShareDetailOf(dto);
                    BigDecimal amountOfTransaction = BigDecimal.ZERO;
                    BigDecimal defaultCurrency = dto.getDefaultCurrencyRate();
                    BigDecimal customCurrency = dto.getCustomCurrencyRate();

                    if (groupShareDetailsDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        amountOfTransaction = amountOfTransaction.add(groupShareDetailsDTO.getAmount().abs());
                    }

                    if (customCurrency.compareTo(BigDecimal.ZERO) > 0) {
                        amountOfTransaction = amountOfTransaction.multiply(customCurrency);
                        actualCost = actualCost.add(amountOfTransaction);
                    } else {
                        amountOfTransaction = amountOfTransaction.multiply(defaultCurrency);
                        actualCost = actualCost.add(amountOfTransaction);
                    }

                    if (dto.getIdCategory().equals(1) || dto.getIdCategory() == 1) {
                        activityCost = activityCost.add(amountOfTransaction);
                    }
                    if (dto.getIdCategory().equals(2) || dto.getIdCategory() == 2) {
                        accommodationCost = accommodationCost.add(amountOfTransaction);
                    }
                    if (dto.getIdCategory().equals(3) || dto.getIdCategory() == 3) {
                        transportationCost = transportationCost.add(amountOfTransaction);
                    }
                    if (dto.getIdCategory().equals(4) || dto.getIdCategory() == 4) {
                        foodCost = foodCost.add(amountOfTransaction);
                    }
                }
            }
        }

        budgetOverViewDTO.setActivityBudget(activityCost);
        budgetOverViewDTO.setAccommodationBudget(accommodationCost);
        budgetOverViewDTO.setTransportationBudget(transportationCost);
        budgetOverViewDTO.setFoodBudget(foodCost);
        budgetOverViewDTO.setActualCost(actualCost);
        return budgetOverViewDTO;
    }

    public static BudgetOverViewDTO getMyImpactOnGroup(List<TransactionDTO> transactionDTOList, int memberSize) {
        BudgetOverViewDTO budgetOverViewDTO = new BudgetOverViewDTO();
        BigDecimal actualMyIndividualGroupCost = BigDecimal.ZERO;
        BigDecimal activityMyIndividualGroupCost = BigDecimal.ZERO;
        BigDecimal accommodationMyIndividualGroupCost = BigDecimal.ZERO;
        BigDecimal transportationMyIndividualGroupCost = BigDecimal.ZERO;
        BigDecimal foodMyIndividualGroupCost = BigDecimal.ZERO;
        BigDecimal size = new BigDecimal(memberSize);

        if (transactionDTOList != null) {
            for (TransactionDTO dto : transactionDTOList) {
                if (dto.getIdType().equals(TransactionType.EXPENSE)) {
                    TransactionDetailDTO groupShareDetailsDTO = TransactionHandler.groupShareDetailOf(dto);
                    BigDecimal amountOfTransaction = BigDecimal.ZERO;
                    BigDecimal groupShareDetails = groupShareDetailsDTO.getAmount();
                    BigDecimal defaultCurrency = dto.getDefaultCurrencyRate();
                    BigDecimal customCurrency = dto.getCustomCurrencyRate();
                    if (groupShareDetailsDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        amountOfTransaction = groupShareDetails.divide(size, RoundingMode.DOWN);
                    }
                    if (customCurrency.compareTo(BigDecimal.ZERO) > 0) {
                        amountOfTransaction = amountOfTransaction.multiply(customCurrency);
                        actualMyIndividualGroupCost = actualMyIndividualGroupCost.add(amountOfTransaction);
                    } else {
                        amountOfTransaction = amountOfTransaction.multiply(defaultCurrency);
                        actualMyIndividualGroupCost = actualMyIndividualGroupCost.add(amountOfTransaction);
                    }

                    if (dto.getIdCategory().equals(1) || dto.getIdCategory() == 1) {
                        activityMyIndividualGroupCost = activityMyIndividualGroupCost.add(amountOfTransaction);
                    }
                    if (dto.getIdCategory().equals(2) || dto.getIdCategory() == 2) {
                        accommodationMyIndividualGroupCost = accommodationMyIndividualGroupCost.add(amountOfTransaction);
                    }
                    if (dto.getIdCategory().equals(3) || dto.getIdCategory() == 3) {
                        transportationMyIndividualGroupCost = transportationMyIndividualGroupCost.add(amountOfTransaction);
                    }
                    if (dto.getIdCategory().equals(4) || dto.getIdCategory() == 4) {
                        foodMyIndividualGroupCost = foodMyIndividualGroupCost.add(amountOfTransaction);
                    }
                }
            }
        }
        budgetOverViewDTO.setActivityBudget(activityMyIndividualGroupCost);
        budgetOverViewDTO.setAccommodationBudget(accommodationMyIndividualGroupCost);
        budgetOverViewDTO.setTransportationBudget(transportationMyIndividualGroupCost);
        budgetOverViewDTO.setFoodBudget(foodMyIndividualGroupCost);
        budgetOverViewDTO.setActualCost(actualMyIndividualGroupCost);
        return budgetOverViewDTO;
    }


    public static BigDecimal myImpactedBalance(TransactionDTO transactionDTO, String firebaseUid) {
        if (transactionDTO != null) {
            BigDecimal impactedBalance = BigDecimal.ZERO;
            for (TransactionDetailDTO transactionDetailDTO : getDividedTransaction(transactionDTO).getTransactionDetailList()) {
                if (transactionDetailDTO.getMember().getPerson().getFirebaseUid().equals(firebaseUid)) {
                    if (transactionDetailDTO.getIdType().equals(TransactionDetailType.RECEIVABLE)) {
                        impactedBalance = impactedBalance.add(transactionDetailDTO.getAmount().abs());
                    } else if (transactionDetailDTO.getIdType().equals(TransactionDetailType.PAYABLE)) {
                        impactedBalance = impactedBalance.subtract(transactionDetailDTO.getAmount().abs());
                    }
                }
            }
            return impactedBalance;
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Data
    @AllArgsConstructor
    private static class SubsetSum {
        private BigDecimal sum;
        private List<TransactionDetailDTO> transactionDetailDTOList;
    }
}
