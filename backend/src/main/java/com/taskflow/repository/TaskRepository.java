package com.taskflow.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.entity.Status;
import com.taskflow.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    public Page<Task> findByStatus(Status status, Pageable pageable);

    public Optional<Task> findById(Long id);
}
