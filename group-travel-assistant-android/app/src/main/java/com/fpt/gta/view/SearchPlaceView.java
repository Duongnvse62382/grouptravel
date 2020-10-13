package com.fpt.gta.view;

import com.fpt.gta.data.dto.PlaceDTO;

import java.util.List;

public interface SearchPlaceView {
    void searchPlaceSS(List<PlaceDTO> placeDTOList);
    void searchPlaceFail(String message);
}
