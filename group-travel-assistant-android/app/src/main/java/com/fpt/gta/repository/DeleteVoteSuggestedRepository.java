package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface DeleteVoteSuggestedRepository {
    void deleteVote(Context context, Integer idSuggested, CallBackData callBackData);
}
