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

    /*
     * GET /api/tasks
     * Returns paginated list with all tasks
     *
     * @param pageable page (0-indexed), size(default 10, max 100)
     *
     * @return Page<Task> with tasks
     */
    @GetMapping
    public Page<Task> getAllTasks(@PageableDefault(size = 10) Pageable pageable) {
        return taskService.getAll(pageable);
    }

    /*
     * GET /api/id
     * Returns a single task with a specific id
     *
     * @param id the id of the task to be found
     *
     * @return the task if found
     */
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    /*
     * POST /api/tasks
     * Create a new task
     *
     * @param task the object of the task to be created
     *
     * @return ResponseEntity with status 201 if task was created or 400 otherwise
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task created = taskService.create(task);
        if (created != null) {
            return ResponseEntity.status(201).body(created);
        } else {
            return ResponseEntity.status(400).build();
        }
    }

    /*
     * @PUT /api/tasks/id
     * Updates the info of a single task with a certain ID
     *
     * @param id the ID of the task to be updated
     *
     * @param task the object containing the updated task information
     *
     * @return a ResponseEntity with 200 status code if successful
     */
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        return taskService.update(id, task);
    }

    /*
     * GET /api/status/status
     * Returns paginated list with all tasks with a certain status
     *
     * @param status the Status to be filtered by
     *
     * @param pageable page (0-indexed), size(default 10, max 100)
     *
     * @return Page<Task> with tasks filtered by the status
     */
    @GetMapping("/status/{status}")
    public Page<Task> filterByStatus(@PathVariable Status status, @PageableDefault(size = 10) Pageable pageable) {
        return taskService.filterByStatus(status, pageable);
    }

    /*
     * DELETE /api/tasks/id
     * Deletes a single task with a certain ID if it is found
     *
     * @param id the id of the task to be deleted
     *
     * @return an empty ReponseEntity with status code depending if the id was found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
