package com.taskflow.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.taskflow.repository.TaskRepository;
import com.taskflow.entity.*;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void run(String... args) throws Exception {
        if (taskRepository.count() == 0) {
            taskRepository.save(Task.builder()
                    .title("Task 1")
                    .description("Bug Fix")
                    .status(Status.PENDING)
                    .assignedTo(1L)
                    .build());

            taskRepository.save(Task.builder()
                    .title("Task 2")
                    .description("Implement feature")
                    .status(Status.PENDING)
                    .assignedTo(3L)
                    .build());

            taskRepository.save(Task.builder()
                    .title("Test feature")
                    .description("Bug Fix")
                    .status(Status.PENDING)
                    .assignedTo(5L)
                    .build());

            taskRepository.save(Task.builder()
                    .title("Task 4")
                    .description("Refactor function")
                    .status(Status.PENDING)
                    .assignedTo(7L)
                    .build());
        }
    }
}
