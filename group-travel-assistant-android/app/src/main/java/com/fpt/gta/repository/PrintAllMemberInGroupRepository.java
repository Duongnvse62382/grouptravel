package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public interface PrintAllMemberInGroupRepository {
    void prinAllMemberInGroup(Context context, int idGroup, CallBackData<List<MemberDTO>> callBackData);
}
