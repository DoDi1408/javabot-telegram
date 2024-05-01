package com.javabot.service;

import com.javabot.models.Task;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

public class TaskServiceImpl implements TaskService{

    private EntityManager entityManager;

    public TaskServiceImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public Task findById(Integer id) {
       return entityManager.find(Task.class, id);
    }

    @Override
    @Transactional
    public void create(Task theTask) {
        entityManager.persist(theTask);
    }

    @Override
    @Transactional
    public void update(Task theTask) {
        entityManager.merge(theTask);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Task theTask = entityManager.find(Task.class, id);
        entityManager.remove(theTask);
    }
}
