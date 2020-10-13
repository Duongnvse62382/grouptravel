package com.fpt.gta.repository;

import android.content.Context;

import com.fpt.gta.webService.CallBackData;

public interface CreateVoteSuggestedRepository {
    void createVote(Context context, Integer idSuggested, CallBackData callBackData);
}
