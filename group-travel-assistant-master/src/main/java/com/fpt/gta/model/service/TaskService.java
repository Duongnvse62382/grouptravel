package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Task;

import java.util.List;

public interface TaskService {
    List<Task> findAllTaskInActivity(String firebaseUid, Integer idActivity);

    List<Task> findAllTaskInGroup(String firebaseUid, Integer idGroup);

    void createNewTask(String firebaseUid, Integer idActivity, Task task);

    void updateTask(String firebaseUid, Task task);

    void deleteTask(String firebaseUid, Integer idTask);

    void changeOrder(String firebaseUid, Integer idTask, Integer order);
}
