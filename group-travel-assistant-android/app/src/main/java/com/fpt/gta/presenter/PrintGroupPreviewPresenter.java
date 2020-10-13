package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.GroupResponseDTO;
import com.fpt.gta.repository.PrintGroupPreviewRepository;
import com.fpt.gta.repository.PrintGroupPreviewRepositoryIml;
import com.fpt.gta.view.PrintGroupPreviewView;
import com.fpt.gta.webService.CallBackData;

public class PrintGroupPreviewPresenter {
    private Context mContex;
    private PrintGroupPreviewView mPrintGroupPreviewView;
    private PrintGroupPreviewRepository mPrintGroupPreviewRepository;

    public PrintGroupPreviewPresenter(Context mContex, PrintGroupPreviewView mPrintGroupPreviewView) {
        this.mContex = mContex;
        this.mPrintGroupPreviewView = mPrintGroupPreviewView;
        this.mPrintGroupPreviewRepository = new PrintGroupPreviewRepositoryIml();
    }

    public void printGroupPreview(String idGroup) {
        mPrintGroupPreviewRepository.printGroupPreview(mContex, idGroup, new CallBackData<GroupResponseDTO>() {
            @Override
            public void onSuccess(GroupResponseDTO groupResponseDTO) {
                mPrintGroupPreviewView.printGroupPreviewSucess(groupResponseDTO);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintGroupPreviewView.printGroupPreviewFail(message);
            }
        });
    }
}
