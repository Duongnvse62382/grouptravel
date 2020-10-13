package com.fpt.gta.model.service.impl;

import com.fpt.gta.model.constant.MemberStatus;
import com.fpt.gta.model.entity.AppInstance;
import com.fpt.gta.model.entity.Member;
import com.fpt.gta.model.repository.AppInstanceRepository;
import com.fpt.gta.model.repository.GroupRepository;
import com.fpt.gta.model.repository.MemberRepository;
import com.fpt.gta.model.service.MessagingService;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FirebaseCloudMessagingServiceImpl implements MessagingService {

    GroupRepository groupRepository;
    MemberRepository memberRepository;
    AppInstanceRepository appInstanceRepository;

    @Autowired
    public FirebaseCloudMessagingServiceImpl(GroupRepository groupRepository, MemberRepository memberRepository, AppInstanceRepository appInstanceRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.appInstanceRepository = appInstanceRepository;
    }

    @Override
    @Async
    public void messageAllInGroupAsync(Integer idGroup, Map<String, String> data) {
        data.put("idGroup", idGroup.toString());

        List<Member> memberList = memberRepository.findAllByIdJoinedGroupAndIdStatus(idGroup, MemberStatus.ACTIVE);
        List<String> idInstantList = new ArrayList<>();
        List<AppInstance> appInstanceList = new ArrayList<>();
        for (Member member : memberList) {
            for (AppInstance appInstance : member.getPerson().getAppInstanceList()) {
                appInstanceList.add(appInstance);
                idInstantList.add(appInstance.getIdInstance());
            }
        }
        try {
            MulticastMessage message = MulticastMessage.builder()
                .putAllData(data)
                .addAllTokens(idInstantList)
                .build();
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        try {
                            appInstanceRepository.deleteById(appInstanceList.get(i).getId());
                        } catch (Exception e) {
                            System.out.println("Delete App Instant FAIL");
                        }
                    }
                }
            }
        } catch (FirebaseMessagingException|IllegalArgumentException e) {
//            e.printStackTrace();
        }
    }
}
