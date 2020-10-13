package com.fpt.gta.data.dto;

import com.fpt.gta.webService.gson.Exclude;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class TransactionDTO implements Serializable {
    private Integer id;
    private String name;
    private Integer idType;
    private Date occurAt;
    private MemberDTO owner;
    private Integer idCategory;
    private List<TransactionDetailDTO> transactionDetailList = new ArrayList<>();
    private List<TransactionDetailDTO> oldTransactionDetailList = new ArrayList<>();
    private List<DocumentDTO> documentList = new ArrayList<>();
    private CurrencyDTO currency;
    private BigDecimal customCurrencyRate = BigDecimal.ZERO;
    private BigDecimal defaultCurrencyRate = BigDecimal.ONE;
    private TripReponseDTO occurTrip;
    private List<MemberDTO> memberList;

    @Data
    public static class TransactionDetailDTO implements Serializable {
        private Integer id;
        private Integer idType;
        private BigDecimal amount = BigDecimal.ZERO;
        private MemberDTO member = new MemberDTO();

        public TransactionDetailDTO() {
        }

        @Exclude
        private boolean isSelected = false;

        @Exclude
        private boolean isModified;

        public TransactionDetailDTO(Integer idType, BigDecimal amount, MemberDTO member) {
            this.idType = idType;
            this.amount = amount;
            this.member = member;
        }

        public TransactionDetailDTO(BigDecimal amount, MemberDTO member) {
            this.amount = amount;
            this.member = member;
        }
    }
}


