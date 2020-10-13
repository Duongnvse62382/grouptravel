package com.fpt.gta.rest.managetransaction;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fpt.gta.rest.managecurrency.CurrencyDTO;
import com.fpt.gta.rest.managetrip.TripDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class TransactionDTO {
    private Integer id;
    private String name;
    private Integer idType;
    private Timestamp occurAt;
    private MemberDTO owner;
    private Integer idCategory;
    private List<TransactionDetailDTO> transactionDetailList;
    private CurrencyDTO currency;
    private BigDecimal customCurrencyRate;
    private BigDecimal defaultCurrencyRate;
    private List<DocumentDTO> documentList;
//    private TripDTO occurTrip;
    private List<MemberDTO> memberList;

    @Data
    public static class TransactionDetailDTO {
        private Integer id;
        private Integer idType;
        private BigDecimal amount;
        private MemberDTO member;
    }

    @Data
    public static class MemberDTO {
        private Integer id;
        private BigDecimal expectedPrice;
        private Integer idRole;
        private Integer idStatus;
        private Boolean isReady;
        private PersonDTO person = new PersonDTO();
    }

    @Data
    public static class PersonDTO {
        private Integer id;
        private String name;
        private String email;
        private String phoneNumber;
        private String photoUri;
        private String firebaseUid;
    }

}
