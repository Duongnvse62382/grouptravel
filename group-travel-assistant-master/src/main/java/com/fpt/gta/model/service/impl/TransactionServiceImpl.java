package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.ForbiddenException;
import com.fpt.gta.exception.NotFoundException;
import com.fpt.gta.exception.UnprocessableEntityException;
import com.fpt.gta.model.constant.FirebaseDatabaseConstant;
import com.fpt.gta.model.constant.MemberStatus;
import com.fpt.gta.model.constant.TransactionDetailType;
import com.fpt.gta.model.constant.TransactionType;
import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.entity.Currency;
import com.fpt.gta.model.repository.CurrencyRepository;
import com.fpt.gta.model.repository.TransactionDetailRepository;
import com.fpt.gta.model.repository.TransactionRepository;
import com.fpt.gta.model.service.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Transactional(rollbackFor = Exception.class)
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionRepository;
    TransactionDetailRepository transactionDetailRepository;
    PersonService personService;
    MemberService memberService;
    GroupService groupService;
    DocumentService documentService;
    AuthenticationService authenticationService;
    CurrencyRepository currencyRepository;
    CurrencyService currencyService;
    ForkJoinPool forkJoinPool;
    ChatMessageService chatMessageService;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionDetailRepository transactionDetailRepository, PersonService personService, MemberService memberService, GroupService groupService, DocumentService documentService, AuthenticationService authenticationService, CurrencyRepository currencyRepository, CurrencyService currencyService, ForkJoinPool forkJoinPool, ChatMessageService chatMessageService) {
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
        this.personService = personService;
        this.memberService = memberService;
        this.groupService = groupService;
        this.documentService = documentService;
        this.authenticationService = authenticationService;
        this.currencyRepository = currencyRepository;
        this.currencyService = currencyService;
        this.forkJoinPool = forkJoinPool;
        this.chatMessageService = chatMessageService;
    }

    public Transaction getTransactionById(Integer idTransaction) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(idTransaction);
        if (optionalTransaction.isPresent()) {
            return optionalTransaction.get();
        } else {
            throw new NotFoundException("Transaction not exist");
        }
    }

    @Override
    public Transaction createTransaction(String firebaseUid, Integer idGroup, Transaction transaction) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Member owner = memberService.getMemberByIdPersonAndIdGroup(person.getId(), idGroup);

        boolean isPermissionGranted = false;

        for (TransactionDetail transactionDetail : transaction.getTransactionDetailList()) {
            boolean isNullMember = true;
            if (transactionDetail.getMember() != null) {
                if (transactionDetail.getMember().getId() != null) {
                    if (transactionDetail.getMember().getId() > 0) {
                        isNullMember = false;
                    }
                }
            }
            if (isNullMember) {
                if (transactionDetail.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    isPermissionGranted = true;
                }
            } else {
                if (owner.getId().equals(transactionDetail.getMember().getId())) {
                    isPermissionGranted = true;
                }
            }
        }

        if (CollectionUtils.isEmpty(transaction.getTransactionDetailList())) {
            throw new ForbiddenException("Transaction must have transaction detail");
        }

        try {
            Currency newCurrency = currencyRepository.findByCode(transaction.getCurrency().getCode()).get();
            transaction.setCurrency(newCurrency);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        //setup transactionDetail entity
        for (TransactionDetail transactionDetail : transaction.getTransactionDetailList()) {
            transactionDetail.setTransaction(transaction);
        }
//        boolean isNullTrip = true;
//        if (transaction.getOccurTrip() != null) {
//            if (transaction.getOccurTrip().getId() != null) {
//                isNullTrip = false;
//            }
//        }
//        if (isNullTrip) {
//            transaction.setOccurTrip(null);
//        }
        transaction.setOwner(owner);

        if (!isValid(transaction)) {
            throw new UnprocessableEntityException("Transaction is not Valid");
        }

        transaction = transactionRepository.save(transaction);

        //prepare transaction detail
        for (TransactionDetail transactionDetail : transaction.getTransactionDetailList()) {
            boolean isNullMember = true;
            if (transactionDetail.getMember() != null) {
                if (transactionDetail.getMember().getId() != null) {
                    if (transactionDetail.getMember().getId() > 0) {
                        isNullMember = false;
                    }
                }
            }
            if (isNullMember) {
                transactionDetail.setMember(null);
            }
        }

        //save transactionDetail
        for (TransactionDetail transactionDetail : transaction.getTransactionDetailList()) {
            transactionDetailRepository.save(transactionDetail);
        }

        return transaction;
    }

    @Override
    public Transaction createTransaction(String firebaseUid, Integer idGroup, Transaction transaction, List<Document> newDocumentList) {
        Transaction createdTransaction = createTransaction(firebaseUid, idGroup, transaction);
        if (newDocumentList != null) {
//            newDocumentList.parallelStream().parallel().forEach(newDocument -> {
//
//            });
            for (Document newDocument : newDocumentList) {
                boolean isNewDocument = true;
                if (newDocument.getId() != null) {
                    if (newDocument.getId() != 0) {
                        isNewDocument = false;
                    }
                }

                if (isNewDocument) {
                    documentService.addNewDocumentTransaction(
                            firebaseUid,
                            createdTransaction,
                            newDocument.getContentType(),
                            newDocument.getUri()
                    );
                } else {
                    documentService.addGroupDocumentToTransaction(
                            firebaseUid,
                            createdTransaction,
                            newDocument.getId()
                    );
                }
            }
        }

        transaction = transactionRepository.save(transaction);


        return createdTransaction;
    }

    @Override
    public Transaction updateTransaction(String firebaseUid, Transaction transaction) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);

        Transaction oldTransaction = getTransactionById(transaction.getId());

        Integer idGroup = oldTransaction.getOwner().getJoinedGroup().getId();
        Member currentMember = memberService.getByIdPersonAndIdGroup(person.getId(), idGroup);

        boolean isPermissionGranted = false;

        for (TransactionDetail transactionDetail : transaction.getTransactionDetailList()) {
            boolean isNullMember = true;
            if (transactionDetail.getMember() != null) {
                if (transactionDetail.getMember().getId() != null) {
                    if (transactionDetail.getMember().getId() > 0) {
                        isNullMember = false;
                    }
                }
            }
            if (isNullMember) {
                if (transactionDetail.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    isPermissionGranted = true;
                }
            } else {
                if (currentMember.getId().equals(transactionDetail.getMember().getId())) {
                    isPermissionGranted = true;
                }
            }
        }

        if (!isPermissionGranted) {
            throw new ForbiddenException("you are not admin or relate to this transaction");
        }

        if (CollectionUtils.isEmpty(transaction.getTransactionDetailList())) {
            throw new ForbiddenException("Transaction must have transaction detail");
        }
        if (!isValid(transaction)) {
            throw new UnprocessableEntityException("Transaction is not valid");
        }
        Member newOwner = memberService.getMemberByIdMember(transaction.getOwner().getId());
        oldTransaction.setOwner(newOwner);
        oldTransaction.setOccurAt(transaction.getOccurAt());
        oldTransaction.setName(transaction.getName());
        oldTransaction.setIdType(transaction.getIdType());
        oldTransaction.setIdCategory(transaction.getIdCategory());
//        boolean isNullTrip = true;
//        if (transaction.getOccurTrip() != null) {
//            if (transaction.getOccurTrip().getId() != null) {
//                isNullTrip = false;
//            }
//        }
//        if (isNullTrip) {
//            oldTransaction.setOccurTrip(null);
//        } else {
//            oldTransaction.setOccurTrip(transaction.getOccurTrip());
//        }
        try {
            Currency newCurrency = currencyRepository.findByCode(transaction.getCurrency().getCode()).get();
            oldTransaction.setCurrency(newCurrency);
        } catch (NullPointerException e) {
        }
        oldTransaction.setCustomCurrencyRate(transaction.getCustomCurrencyRate());
        oldTransaction = transactionRepository.save(oldTransaction);

        //remove old entity
        for (TransactionDetail oldTransactionDetail : oldTransaction.getTransactionDetailList()) {
            transactionDetailRepository.deleteById(oldTransactionDetail.getId());
        }

        //setup transactionDetail entity
        for (TransactionDetail transactionDetail : transaction.getTransactionDetailList()) {

            boolean isNullMember = true;
            if (transactionDetail.getMember() != null) {
                if (transactionDetail.getMember().getId() != null) {
                    if (transactionDetail.getMember().getId() > 0) {
                        isNullMember = false;
                    }
                }
            }
            if (isNullMember) {
                transactionDetail.setMember(null);
            }
            transactionDetail.setTransaction(transaction);

        }
        List<TransactionDetail> newTransactionDetailList = new ArrayList<>();
        //save transactionDetail
        for (TransactionDetail transactionDetail : transaction.getTransactionDetailList()) {
            newTransactionDetailList.add(transactionDetailRepository.save(transactionDetail));
        }
        oldTransaction.setTransactionDetailList(newTransactionDetailList);

        return oldTransaction;
    }

    @Override
    public Transaction updateTransaction(String firebaseUid, Transaction transaction, List<Document> uploadDocumentList) {
        Transaction oldTransaction = updateTransaction(firebaseUid, transaction);
        List<Document> oldDocumentList = documentService.findAllDocumentTransaction(firebaseUid, transaction.getId());
        if (uploadDocumentList != null) {
            for (Document uploadDocument : uploadDocumentList) {
                boolean isNewDocument = true;
                if (uploadDocument.getId() != null) {
                    if (uploadDocument.getId() != 0) {
                        isNewDocument = false;
                    }
                }
                if (isNewDocument) {
                    documentService.addNewDocumentTransaction(
                            firebaseUid,
                            oldTransaction.getId(),
                            uploadDocument.getContentType(),
                            uploadDocument.getUri());
                } else {
                    documentService.addGroupDocumentToTransaction(
                            firebaseUid,
                            oldTransaction.getId(),
                            uploadDocument.getId());
                }
            }
            for (Document oldDocument : oldDocumentList) {
                boolean isNotContain = true;
                for (Document newDocument : uploadDocumentList) {
                    if (oldDocument.getId().equals(newDocument.getId())) {
                        isNotContain = false;
                    }
                }
                if (isNotContain) {
                    documentService.deleteDocumentTransaction(firebaseUid, oldDocument.getId());
                }
            }
        }

        return oldTransaction;
    }

    @Override
    public List<Transaction> findAllTransactionInGroup(String firebaseUid, Integer idGroup) {
        List<Transaction> transactionList = transactionRepository.findAllTransactionByIdGroup(idGroup);
        Group group = groupService.getGroupById(idGroup);
        authenticationService.checkJoinedGroupByFirebaseUid(firebaseUid, idGroup);
        IntStream.of(1, 2).parallel().forEach(flag -> {
            if (flag == 1) {
                transactionList.parallelStream().forEach(transaction -> {
                    transaction.setDocumentList(
                            documentService.findAllDocumentTransaction(firebaseUid,
                                    transaction.getId()));
                });
            } else if (flag == 2) {
                transactionList.forEach(transaction -> {
                    transaction.setDefaultCurrencyRate(
                            currencyService.getCurrencyRate(
                                    transaction.getCurrency().getCode(),
                                    group.getCurrency().getCode()
                            ).getRate()
                    );
                });
            }
        });
        for (Transaction transaction : transactionList) {
            List<Member> memberList = new ArrayList<>();
            group.getMemberList().size();
            for (Member member : group.getMemberList()) {
                if (member.getIdStatus().equals(MemberStatus.ACTIVE)) {
                    memberList.add(member);
                }
            }
            transaction.setMemberList(memberList);
        }
        return transactionList;
    }

    @Override
    public Transaction findTransactionById(String firebaseUid, Integer idTransaction) {
        Transaction transaction = transactionRepository.findById(idTransaction).get();
        transaction.setDocumentList(documentService.findAllDocumentTransaction(firebaseUid, transaction.getId()));
        Currency groupCurrency = transaction.getOwner().getJoinedGroup().getCurrency();
        transaction.setDefaultCurrencyRate(currencyService.getCurrencyRate(groupCurrency.getCode(), transaction.getCurrency().getCode()).getRate());
        List<Member> memberList = new ArrayList<>();
        transaction.getOwner().getJoinedGroup().getMemberList().size();
        for (Member member : transaction.getOwner().getJoinedGroup().getMemberList()) {
            if (member.getIdStatus().equals(MemberStatus.ACTIVE)) {
                memberList.add(member);
            }
        }
        transaction.setMemberList(memberList);
        return transaction;
    }

    @Override
    public void deleteTransaction(String firebaseUid, Integer idTransaction) {
        transactionRepository.deleteById(idTransaction);
    }

    public static List<TransactionDetail> participantListOf(Transaction transaction) {
        Integer idType = transaction.getIdType();

        List<TransactionDetail> transactionDetailWithOutGroupShare = new ArrayList<>();
        for (TransactionDetail transactionDetail : transaction.getTransactionDetailList()) {
            if (!isGroupShareDetail(transactionDetail)) {
                transactionDetailWithOutGroupShare.add(transactionDetail);
            }
        }

        List<TransactionDetail> result = new ArrayList<>();
        switch (idType) {
            case TransactionType.EXPENSE:
            case TransactionType.TRANSFER:
                for (TransactionDetail transactionDetail : transactionDetailWithOutGroupShare) {
                    if (transactionDetail.getIdType().equals(TransactionDetailType.PAYABLE)) {
                        result.add(transactionDetail);
                    }
                }
                break;
            case TransactionType.INCOME:
                for (TransactionDetail transactionDetail : transactionDetailWithOutGroupShare) {
                    if (transactionDetail.getIdType().equals(TransactionDetailType.RECEIVABLE)) {
                        result.add(transactionDetail);
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + idType);
        }
        return result;
    }

    public static TransactionDetail payerDetailOf(Transaction transactionDTO) {

        int idType = transactionDTO.getIdType();
        switch (idType) {
            case TransactionType.EXPENSE:
            case TransactionType.TRANSFER:
                for (TransactionDetail transactionDetailDTO : transactionDTO.getTransactionDetailList()) {
                    if (transactionDetailDTO.getIdType().equals(TransactionDetailType.CASH)) {
                        if (transactionDetailDTO.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                            return transactionDetailDTO;
                        }
                    }
                }
                break;

            case TransactionType.INCOME:
                for (TransactionDetail transactionDetailDTO : transactionDTO.getTransactionDetailList()) {
                    if (transactionDetailDTO.getIdType().equals(TransactionDetailType.CASH)) {
                        if (transactionDetailDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                            return transactionDetailDTO;
                        }
                    }
                }
                break;

            default:
                throw new IllegalStateException("TransactionType: " + idType);
        }
        throw new RuntimeException("get value error !!!");
    }

    public static TransactionDetail groupShareDetailOf(Transaction transaction) {
        for (TransactionDetail transactionDetailDTO : transaction.getTransactionDetailList()) {
            if (isGroupShareDetail(transactionDetailDTO)) {
                return transactionDetailDTO;
            }
        }
        return new TransactionDetail();
    }

    public static boolean isGroupShareDetail(TransactionDetail transactionDetail) {
        if (transactionDetail.getMember() == null) {
            return true;
        }
        if (transactionDetail.getMember().getId() == 0) {
            return true;
        }
        return false;
    }


    private static final Map<Integer, BigDecimal> oneSideSign = Stream.of(new Object[][]{
            {TransactionDetailType.CASH, BigDecimal.valueOf(1)},
            {TransactionDetailType.EXPENSE, BigDecimal.valueOf(1)},
            {TransactionDetailType.INCOME, BigDecimal.valueOf(-1)},
            {TransactionDetailType.PAYABLE, BigDecimal.valueOf(-1)},
            {TransactionDetailType.RECEIVABLE, BigDecimal.valueOf(1)}
    }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (BigDecimal) data[1]));

    private static Boolean isValid(Transaction transactionDTO) {
        List<TransactionDetail> transactionDetailDTOList = transactionDTO.getTransactionDetailList();

        if (transactionDetailDTOList == null) {
            return false;
        }
        if (transactionDetailDTOList.size() == 0) {
            return false;
        }

        Map<Integer, BigDecimal> balanceMap = new HashMap<>();

        for (TransactionDetail transactionDetailDTO : transactionDetailDTOList) {
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
        for (TransactionDetail transactionDetailDTO : participantListOf(transactionDTO)) {
            totalParticipantAmount = totalParticipantAmount.add(transactionDetailDTO.getAmount().abs());
        }

        TransactionDetail groupShareDetail = groupShareDetailOf(transactionDTO);
        BigDecimal groupShareAmount = BigDecimal.ZERO;
        if (groupShareAmount != null) {
            if (groupShareDetail.getAmount() != null) {
                groupShareAmount = groupShareDetail.getAmount();
            }
        }
        totalParticipantAmount = totalParticipantAmount.add(groupShareAmount.abs());

        if (payerAmount.compareTo(totalParticipantAmount) != 0) {
            return false;
        }

        return true;
    }

}
