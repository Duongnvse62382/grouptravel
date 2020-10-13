package com.fpt.gta.data.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;


@Data
public class ListMemberDTO implements Serializable {
    private List<MemberDTO> memberDTOList;
}
