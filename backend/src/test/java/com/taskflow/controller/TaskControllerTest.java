package com.taskflow.controller;

import com.taskflow.entity.Status;
import com.taskflow.entity.Task;
import com.taskflow.exception.TaskNotFoundException;
import com.taskflow.service.TaskService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @SneakyThrows
    @Test
    public void testGetAllTasks() {
        Task task1 = new Task(1L, "Task", "Bug fix", Status.PENDING, 1L,
                LocalDateTime.now(), LocalDateTime.now());
        Page<Task> page = new PageImpl<>(List.of(task1));

        when(taskService.getAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/tasks")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Task"));
    }

    @SneakyThrows
    @Test
    public void testCreateTaskValidationError() {
        String invalidJson = "{ \"description\": \"No title\" }";

        mockMvc.perform(
                        post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void testTaskNotFoundError() {
        long invalidId = 999L;
        when(taskService.getById(invalidId)).thenThrow(
                new TaskNotFoundException(invalidId));

        mockMvc.perform(get("/api/tasks/" + invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Status").value(404))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @SneakyThrows
    @Test
    public void testValidationError() {
        String invalidJson = "{}";

        mockMvc.perform(
                        post("/api" + "/tasks").contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Status").value(400));
    }
}
