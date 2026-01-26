package com.taskflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.entity.Status;
import com.taskflow.entity.Task;
import com.taskflow.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping
    public Page<Task> getAllTasks(@PageableDefault(size = 10) Pageable pageable) {
        return taskService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task created = taskService.create(task);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        return taskService.update(id, task);
    }

    @GetMapping("/status/{status}")
    public Page<Task> filterByStatus(@PathVariable Status status, @PageableDefault(size = 10) Pageable pageable) {
        return taskService.filterByStatus(status, pageable);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@Valid @PathVariable Long id) {
        taskService.delete(id);
    }
}
