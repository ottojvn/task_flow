package com.taskflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}
