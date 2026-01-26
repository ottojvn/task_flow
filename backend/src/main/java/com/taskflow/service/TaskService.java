package com.taskflow.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.entity.Status;
import com.taskflow.entity.Task;
import com.taskflow.exception.TaskNotFoundException;
import com.taskflow.repository.TaskRepository;

import jakarta.validation.Valid;

@Service
@Transactional
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Page<Task> getAll(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task create(@Valid Task task) {
        task.setId(null);
        return taskRepository.save(task);
    }

    public Task update(Long id, @Valid Task task) {
        Task existing = getById(id);
        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setStatus(task.getStatus());
        existing.setAssignedTo(task.getAssignedTo());
        return taskRepository.save(existing);
    }

    public void delete(Long id) {
        Task task = getById(id);
        taskRepository.delete(task);
    }

    public Page<Task> filterByStatus(Status status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }
}
