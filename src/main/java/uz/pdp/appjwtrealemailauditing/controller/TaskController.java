package uz.pdp.appjwtrealemailauditing.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjwtrealemailauditing.payload.TaskDto;
import uz.pdp.appjwtrealemailauditing.payload.ApiResponse;
import uz.pdp.appjwtrealemailauditing.service.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public HttpEntity<?> save(@RequestBody TaskDto taskDto) {
        ApiResponse response = taskService.save(taskDto);
        return ResponseEntity.status(response.isSuccess() ? 202 : 401).body(response);
    }

    @PutMapping("/{id}")
    public HttpEntity<?> completeTask(@PathVariable Integer id, @RequestParam Integer taskStatus) {
        ApiResponse response = taskService.completeTask(id, taskStatus);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.UNAUTHORIZED).body(response);
    }

    // xodimlarga berilgan vazifalarni o’z vaqtida bajargani yoki o’z vaqtida bajara olmayotgani xaqida malumotlar
    @GetMapping
    public HttpEntity<?> checkEmployeeTask(@RequestParam UUID employeeId, @RequestParam Integer taskStatus) {
        ApiResponse response = taskService.checkEmployeeTask(employeeId, taskStatus);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.UNAUTHORIZED).body(response);
    }

}
