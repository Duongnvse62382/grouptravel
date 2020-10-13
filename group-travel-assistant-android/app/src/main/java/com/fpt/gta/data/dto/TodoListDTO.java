package com.fpt.gta.data.dto;

import com.fpt.gta.webService.gson.Exclude;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
@Data
public class TodoListDTO implements Serializable {
    private Integer id;
    private String nameWork;
    private MemberDTO owner;

    private List<MemberDTO>  memberList = new ArrayList<>();

    @Data
    public static class MemberDTO implements Serializable {
        private Integer id;
        private BigDecimal expectedPrice;
        private Integer idRole;
        private Integer idStatus;
    }
    @Exclude
    private boolean isSelected;

    public TodoListDTO(Integer id, String nameWork) {
        this.id = id;
        this.nameWork = nameWork;
    }

    public TodoListDTO(Integer id, String nameWork, MemberDTO owner) {
        this.id = id;
        this.nameWork = nameWork;
        this.owner = owner;
    }
}
