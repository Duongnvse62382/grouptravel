package com.fpt.gta.view;

import com.fpt.gta.data.dto.MemberDTO;

import java.util.List;

public interface PrintMemberInGroupView {
     void PrintMemberSuccess(List<MemberDTO> memberDTOList);
     void PrintMemberFail(String message);
}
