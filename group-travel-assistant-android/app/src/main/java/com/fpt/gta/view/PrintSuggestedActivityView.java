package com.fpt.gta.view;

import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;

import java.util.List;

public interface PrintSuggestedActivityView {
    void getSuggestedSuccess(List<SuggestedActivityResponseDTO> suggestedActivityResponseDTOList);
    void getSuggestedFail(String message);
}
