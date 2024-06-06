package com.javabot.serviceimp;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.javabot.models.Task;
import com.javabot.service.TaskService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
    @Override
    public List<Task> allEmployeeTasks(Integer id) {
        String sqlQuery = "SELECT t FROM Task t WHERE t.employee.id =:employeeId";
        
        TypedQuery<Task> theQuery = entityManager.createQuery(sqlQuery,Task.class);
        theQuery.setParameter("employeeId", id);
        return theQuery.getResultList();
    }



    @Override
    public List<Task> allTeamTasks(Integer teamId) {
        String sqlQuery = "SELECT t FROM Task t " +
                        "JOIN Employee e ON t.employee.id = e.id " +
                        "WHERE e.team = :teamId";
        
        TypedQuery<Task> theQuery = entityManager.createQuery(sqlQuery, Task.class);
        theQuery.setParameter("teamId", teamId);
        return theQuery.getResultList();
    }

    @Override
    public List<Task> toDoStateTasks(Integer id, Integer state) {
        String sqlQuery = "SELECT t FROM Task t WHERE t.employee.id =:employeeId AND t.stateTask = :taskState";

        TypedQuery<Task> theQuery = entityManager.createQuery(sqlQuery,Task.class);
        theQuery.setParameter("employeeId", id);
        theQuery.setParameter("taskState", state);
        return theQuery.getResultList();
    }  

    @Override
    @Transactional
    public void deleteAllEmployeeTasks(Integer id) {
        String sqlQuery = "DELETE FROM Task t WHERE t.employee.id =:employeeId";
        
        Query theQuery = entityManager.createQuery(sqlQuery).setParameter("employeeId", id);
        theQuery.executeUpdate();
    }

    @Override
    public Task cleanUpForFront(Task task){
        if (task.getDescription() == null){
            task.setDescription("This task has no description");
        }
        long millisecondsInYear = 1000L * 60 * 60 * 24 * 365;
        if (task.getDueDate() == null){
            Date dueDate = new Date(task.getDueDate().getTime() + (millisecondsInYear * 100));
            task.setDueDate(dueDate);
        }
        if (task.getStartDate() == null){
            Date startDate = new Date(task.getDueDate().getTime() - (millisecondsInYear * 100));
            task.setStartDate(startDate);
        }
        if (task.getTitle() == null){
            task.setTitle("This task has no title");
        }
        task.getEmployee().setPassword("hidden");
        return task;
    }
}
