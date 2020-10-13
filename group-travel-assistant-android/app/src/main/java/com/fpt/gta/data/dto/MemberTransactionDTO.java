package com.fpt.gta.data.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MemberTransactionDTO implements Serializable {
    private String memberName;
    private double MemberTransactionMoney;

    public MemberTransactionDTO(String memberName, double memberTransactionMoney) {
        this.memberName = memberName;
        MemberTransactionMoney = memberTransactionMoney;
    }
}
