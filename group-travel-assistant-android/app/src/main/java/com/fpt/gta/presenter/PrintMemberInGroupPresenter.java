package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.repository.PrintAllMemberInGroupRepository;
import com.fpt.gta.repository.PrintAllMemberInGroupRepositoryIml;
import com.fpt.gta.view.PrintMemberInGroupView;
import com.fpt.gta.webService.CallBackData;

import java.util.List;

public class PrintMemberInGroupPresenter {
    private Context mContext;
    private PrintMemberInGroupView mPrintMemberInGroupView;
    private PrintAllMemberInGroupRepository mPrintAllMemberInGroupRepository;

    public PrintMemberInGroupPresenter(Context mContext, PrintMemberInGroupView mPrintMemberInGroupView) {
        this.mContext = mContext;
        this.mPrintMemberInGroupView = mPrintMemberInGroupView;
        this.mPrintAllMemberInGroupRepository = new PrintAllMemberInGroupRepositoryIml();
    }

    public void printMemberInGroup(int idGroup){
        mPrintAllMemberInGroupRepository.prinAllMemberInGroup(mContext, idGroup, new CallBackData<List<MemberDTO>>() {
            @Override
            public void onSuccess(List<MemberDTO> memberDTOList) {
                mPrintMemberInGroupView.PrintMemberSuccess(memberDTOList);
            }

            @Override
            public void onSuccessString(String mess) {

            }

            @Override
            public void onFail(String message) {
                mPrintMemberInGroupView.PrintMemberFail(message);
            }
        });
    }
}
