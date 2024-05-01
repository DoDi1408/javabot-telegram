package com.javabot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.javabot.models.Manager;
import com.javabot.models.Task;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Service
public class ManagerServiceImpl implements ManagerService{

    private EntityManager entityManager;

    public ManagerServiceImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public Manager findById(Integer id) {
        return entityManager.find(Manager.class, id);
    }
    
    @Override
    @Transactional
    public void create(Manager theManager) {
        entityManager.persist(theManager);
    }

    @Override
    @Transactional
    public void update(Manager theManager) {
        entityManager.merge(theManager);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Manager theManager = entityManager.find(Manager.class, id);
        entityManager.remove(theManager);
    }

    @Override
    public List<Task> allTeamTasks(Integer teamId) {
    String sqlQuery = "SELECT t FROM Task t " +
                      "JOIN Employee e ON t.employee.id = e.id " +
                      "WHERE e.team.id = :teamId";
    
    TypedQuery<Task> theQuery = entityManager.createQuery(sqlQuery, Task.class);
    theQuery.setParameter("teamId", teamId);
    
    return theQuery.getResultList();

    }
}