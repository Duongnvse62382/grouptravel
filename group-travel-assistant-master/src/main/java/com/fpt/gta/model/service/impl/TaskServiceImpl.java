package com.fpt.gta.model.service.impl;

import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.repository.*;
import com.fpt.gta.model.service.AuthenticationService;
import com.fpt.gta.model.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
public class TaskServiceImpl implements TaskService {
    TaskRepository taskRepository;
    TaskAssignmentRepository taskAssignmentRepository;
    ActivityRepository activityRepository;
    MemberRepository memberRepository;
    GroupRepository groupRepository;
    AuthenticationService authenticationService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TaskAssignmentRepository taskAssignmentRepository, ActivityRepository activityRepository, MemberRepository memberRepository, GroupRepository groupRepository, AuthenticationService authenticationService) {
        this.taskRepository = taskRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.activityRepository = activityRepository;
        this.memberRepository = memberRepository;
        this.authenticationService = authenticationService;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<Task> findAllTaskInActivity(String firebaseUid, Integer idActivity) {
        Activity activity = activityRepository.findById(idActivity).get();
        activity.getTaskList().size();
        for (Task task : activity.getTaskList()) {
            task.getTaskAssignmentList().size();
        }
        return activity.getTaskList();
    }

    @Override
    public List<Task> findAllTaskInGroup(String firebaseUid, Integer idGroup) {
        Group group = groupRepository.findById(idGroup).get();
        authenticationService.checkJoinedGroupByFirebaseUid(firebaseUid, idGroup);
        group.getTaskList().size();
        for (Task task : group.getTaskList()) {
            task.getTaskAssignmentList().size();
        }
        return group.getTaskList();
    }

    @Override
    public void createNewTask(String firebaseUid, Integer idActivity, Task task) {
        Task newTask = new Task();
        Activity activity;
        Trip trip;
        Group group;
        if (idActivity > 0) {
            activity = activityRepository.findById(idActivity).get();
            trip = activity.getPlan().getTrip();
            group = trip.getGroup();
            newTask.setOrder(activity.getTaskList().size());
        } else {
            activity = task.getActivity();
            trip = task.getTrip();
            group = task.getGroup();
        }
        newTask.setActivity(activity);
        newTask.setTrip(trip);
        newTask.setGroup(group);

        newTask.setName(task.getName());
        newTask.setIdStatus(task.getIdStatus());

        newTask = taskRepository.save(newTask);

        if (task.getTaskAssignmentList() != null) {
            for (TaskAssignment taskAssignment : task.getTaskAssignmentList()) {
                Member member = taskAssignment.getMember();
                member = memberRepository.findById(member.getId()).get();
                taskAssignment.setMember(member);
                taskAssignment.setTask(newTask);

                taskAssignmentRepository.save(taskAssignment);
            }
        }
    }

    @Override
    public void updateTask(String firebaseUid, Task task) {
        Task oldTask = taskRepository.findById(task.getId()).get();
        oldTask.setName(task.getName());
        oldTask.setIdStatus(task.getIdStatus());

        oldTask.setTrip(task.getTrip());
        oldTask.setActivity(task.getActivity());

        oldTask = taskRepository.save(oldTask);

        if (oldTask.getTaskAssignmentList() != null) {
            for (TaskAssignment taskAssignment : oldTask.getTaskAssignmentList()) {
                taskAssignmentRepository.deleteById(taskAssignment.getId());
            }
        }
        taskAssignmentRepository.flush();
        if (task.getTaskAssignmentList() != null) {
            for (TaskAssignment taskAssignment : task.getTaskAssignmentList()) {
                Member member = taskAssignment.getMember();
                member = memberRepository.findById(member.getId()).get();
                taskAssignment.setMember(member);
                taskAssignment.setTask(oldTask);
                try {
                    taskAssignmentRepository.save(taskAssignment);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void deleteTask(String firebaseUid, Integer idTask) {
        taskRepository.deleteById(idTask);
    }

    @Override
    public void changeOrder(String firebaseUid, Integer idOldTask, Integer order) {
        Task oldTask = taskRepository.findById(idOldTask).get();
        List<Task> taskList = oldTask.getActivity().getTaskList();
        for (Task task : taskList) {
            if (task.getId().compareTo(idOldTask) == 0) {
                task.setOrder(order);
            } else if (task.getOrder() >= order) {
                task.setOrder(task.getOrder() + 1);
            }
        }

        taskList.sort((o1, o2) -> o1.getOrder().compareTo(o2.getOrder()));

        for (int i = 0; i < taskList.size(); i++) {
            taskList.get(i).setOrder(i);
        }

        for (Task task : taskList) {
            taskRepository.save(task);
        }
    }
}
