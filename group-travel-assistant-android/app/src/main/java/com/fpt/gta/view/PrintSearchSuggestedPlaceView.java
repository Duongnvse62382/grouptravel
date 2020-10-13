package com.fpt.gta.view;

import com.fpt.gta.data.dto.PlaceDTO;

import java.util.List;

public interface PrintSearchSuggestedPlaceView {
    void getSearchSuggestedPlaceSS(List<PlaceDTO> placeDTOList);
    void getSearchSuggestedPlaceFail(String messageFail);
}
