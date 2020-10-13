package com.fpt.gta.util;

import com.fpt.gta.data.dto.SuggestedActivityResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListUtil {

    public static List<SuggestedActivityResponseDTO> convertSetToList(Set<SuggestedActivityResponseDTO> set) {
        List<SuggestedActivityResponseDTO> list = new ArrayList<>();
        for (SuggestedActivityResponseDTO t : set)
            list.add(t);
        return list;
    }
}
