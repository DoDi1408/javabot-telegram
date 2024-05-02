package com.javabot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.javabot.models.Task;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

    private EntityManager entityManager;

    public TaskServiceImpl(EntityManager entityManager) {
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

    public List<Task> findByEmployeeId(Integer employeeId) {
        String sqlQuery = "SELECT t FROM Task t " +
                "WHERE t.employee.id = :employeeId";

        TypedQuery<Task> theQuery = entityManager.createQuery(sqlQuery, Task.class);
        theQuery.setParameter("employeeId", employeeId);

        return theQuery.getResultList();
    }
}
