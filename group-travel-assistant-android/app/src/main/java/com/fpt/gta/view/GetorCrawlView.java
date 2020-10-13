package com.fpt.gta.view;

import com.fpt.gta.data.dto.PlaceDTO;

public interface GetorCrawlView {
    void crawlSuccess(PlaceDTO placeDTO);
    void crawlFail(String messageFail);
}
