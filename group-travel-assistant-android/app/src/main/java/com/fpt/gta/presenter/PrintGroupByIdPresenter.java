package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.repository.PrintGroupByIdRepository;
import com.fpt.gta.repository.PrintGroupByIdRepositoryIml;
import com.fpt.gta.view.PrintGroupByIdView;
import com.fpt.gta.webService.CallBackData;

public class PrintGroupByIdPresenter {
    private Context mContext;
    private PrintGroupByIdView mPrintGroupByIdView;
    private PrintGroupByIdRepository mPrintGroupByIdRepository;

    public PrintGroupByIdPresenter(Context mContext, PrintGroupByIdView mPrintGroupByIdView) {
        this.mContext = mContext;
        this.mPrintGroupByIdView = mPrintGroupByIdView;
        this.mPrintGroupByIdRepository = new PrintGroupByIdRepositoryIml();
    }

    public void getGroupById(Integer idGroup){
        this.mPrintGroupByIdRepository.getGroupById(mContext, idGroup, new CallBackData<GroupResponseDTO>() {
            @Override
            public void onSuccess(GroupResponseDTO groupResponseDTO) {
                mPrintGroupByIdView.printGroupByIdSS(groupResponseDTO);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintGroupByIdView.printGroupByIdFail(message);
            }
        });
    }
}
