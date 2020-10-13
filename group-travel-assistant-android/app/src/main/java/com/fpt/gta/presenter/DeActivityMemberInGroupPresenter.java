package com.fpt.gta.presenter;

import android.content.Context;

import com.fpt.gta.repository.DeActiveMemberInGroupRepository;
import com.fpt.gta.repository.DeActiveMemberInGroupRepositoryIml;
import com.fpt.gta.view.DeActivityMemberInGroupView;
import com.fpt.gta.webService.CallBackData;

public class DeActivityMemberInGroupPresenter {
    private Context mContext;
    private DeActiveMemberInGroupRepository deActiveMemberInGroupRepository;
    private DeActivityMemberInGroupView deActivityMemberInGroupView;

    public DeActivityMemberInGroupPresenter(Context mContext, DeActivityMemberInGroupView deActivityMemberInGroupView) {
        this.mContext = mContext;
        this.deActivityMemberInGroupView = deActivityMemberInGroupView;
        this.deActiveMemberInGroupRepository = new DeActiveMemberInGroupRepositoryIml();
    }

    public void deActiveMemberInGroup(Integer idGroup, Integer idMember){
        deActiveMemberInGroupRepository.deActiveMemberInGroup(mContext, idGroup, idMember, new CallBackData() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onSuccessString(String mess) {
                deActivityMemberInGroupView.deActivityMemberInGroupSuccess("Success");
            }

            @Override
            public void onFail(String message) {
                deActivityMemberInGroupView.deActivityMemberInGroupFail(message);
            }
        });
    }
}
