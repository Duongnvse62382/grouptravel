package com.fpt.gta.view;

import com.fpt.gta.data.dto.PlaceDTO;

import java.util.List;

public interface SearchPlaceCitiesView {
    void searchPlaceCitiesSS(List<PlaceDTO> placeDTOList);
    void searchPlaceCitiesFail(String message);
}
