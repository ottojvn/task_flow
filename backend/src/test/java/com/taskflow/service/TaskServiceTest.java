package com.taskflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.taskflow.entity.Status;
import com.taskflow.entity.Task;
import com.taskflow.exception.TaskNotFoundException;
import com.taskflow.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task(
                1L,
                "Task 1",
                "Bug fix",
                Status.PENDING,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now());
        Task task2 = new Task(
                2L,
                "Task 2",
                "Write documentation",
                Status.PENDING,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now());

        Page<Task> page = new PageImpl<>(Arrays.asList(task1, task2));

        when(taskRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Task> result = taskService.getAll(Pageable.ofSize(10));

        assertEquals(2, result.getContent().size());
        assertEquals("Task 1", result.getContent().get(0).getTitle());
        assertEquals("Task 2", result.getContent().get(1).getTitle());

        verify(taskRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testGetByIdNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> {
            taskService.getById(999L);
        });
    }

    @Test
    public void testCreateTask() {
        Task newTask = new Task(
                null,
                "Test task",
                "Desc",
                Status.PENDING,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now());
        Task saved = new Task(
                5L,
                "Test task",
                "Desc",
                Status.PENDING,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task result = taskService.create(newTask);
        assertEquals(5L, result.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}
