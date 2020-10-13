package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;
import com.fpt.gta.data.dto.GroupResponseDTO;

import java.util.List;


public interface PrintGroupReponsitory {
    void printGroup(Context context,CallBackData<List<GroupResponseDTO>> callBackData);
}
