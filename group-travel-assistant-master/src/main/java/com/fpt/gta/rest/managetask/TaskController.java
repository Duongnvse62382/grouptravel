package com.fpt.gta.rest.managetask;

import com.fpt.gta.model.entity.Task;
import com.fpt.gta.model.service.TaskService;
import com.google.firebase.auth.FirebaseToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    ModelMapper modelMapper;
    TaskService taskService;

    @Autowired
    public TaskController(ModelMapper modelMapper, TaskService taskService) {
        this.modelMapper = modelMapper;
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDTO> findAllTaskInActivity(
            @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
            @RequestParam String idActivity
    ) {
        return Arrays.asList(
                modelMapper.map(
                        taskService.findAllTaskInActivity(firebaseToken.getUid(),
                                Integer.parseInt(idActivity)), TaskDTO[].class
                )
        );
    }

    @GetMapping("/groupTask")
    public List<TaskDTO> findAllTaskInGroup(
            @RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
            @RequestParam String idGroup) {
        return Arrays.asList(
                modelMapper.map(
                        taskService.findAllTaskInGroup(firebaseToken.getUid(),
                                Integer.parseInt(idGroup)), TaskDTO[].class
                )
        );
    }

    @PostMapping
    public void createTask(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                           @RequestBody TaskDTO taskDTO,
                           @RequestParam Integer idActivity
    ) {
        taskService.createNewTask(firebaseToken.getUid(), idActivity, modelMapper.map(taskDTO, Task.class));
    }

    @PutMapping
    public void updateTask(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                           @RequestBody TaskDTO taskDTO) {

        taskService.updateTask(firebaseToken.getUid(), modelMapper.map(taskDTO, Task.class));
    }

    @DeleteMapping("/{idTask}")
    public void deleteTask(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                           @PathVariable String idTask) {
        taskService.deleteTask(firebaseToken.getUid(), Integer.parseInt(idTask));
    }

    @PatchMapping("/changeOrder")
    public void changeOrder(@RequestAttribute("FirebaseToken") FirebaseToken firebaseToken,
                            @RequestParam String idTask,
                            @RequestParam String order
    ) {
        taskService.changeOrder(firebaseToken.getUid(), Integer.parseInt(idTask), Integer.parseInt(order));
    }
}
