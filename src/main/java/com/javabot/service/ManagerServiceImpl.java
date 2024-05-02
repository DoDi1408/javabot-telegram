package com.javabot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.javabot.models.Manager;
import com.javabot.models.Task;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Service
public class ManagerServiceImpl implements ManagerService{

    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    public List<Task> allTeamTasks(Integer teamId) {
    String sqlQuery = "SELECT t FROM Task t " +
                      "JOIN Employee e ON t.employee.id = e.id " +
                      "WHERE e.team.id = :teamId";
    
    TypedQuery<Task> theQuery = entityManager.createQuery(sqlQuery, Task.class);
    theQuery.setParameter("teamId", teamId);
    
    return theQuery.getResultList();
    }
    
    @Transactional
    public void createManager(String firstName, String lastName, String telegramId, String teamName) {
        String insertTeamQuery = "INSERT INTO team (name_team) VALUES (?)";
        String insertEmployeeQuery = "INSERT INTO employee (first_name, last_name, id_team, telegram_id) " + "VALUES (?, ?, ?, ?)";
        String selectTeamIdQuery = "SELECT team_seq.CURRVAL FROM DUAL";    
        String selectEmployeeIdQuery = "SELECT employee_seq.CURRVAL FROM DUAL";
        String insertManagerQuery = "INSERT INTO TELEGRAM_BOT_USER.manager (id_employee, id_team) VALUES (?, ?)";
        jdbcTemplate.update(insertTeamQuery, teamName);
        Long id_teamVAL = jdbcTemplate.queryForObject(selectTeamIdQuery, Long.class);
        jdbcTemplate.update(insertEmployeeQuery, firstName, lastName, id_teamVAL ,telegramId);
        Long id_employeeVAL = jdbcTemplate.queryForObject(selectEmployeeIdQuery, Long.class);
        jdbcTemplate.update(insertManagerQuery, id_employeeVAL, id_teamVAL);
 
    }

    @Transactional
    public Manager findByEmployeeId(Integer employeeId){
        String sqlQuery = "SELECT m FROM Manager m " + "WHERE m.employee.id = :employeeId";
        TypedQuery<Manager> theQuery = entityManager.createQuery(sqlQuery, Manager.class);
        theQuery.setParameter("employeeId", employeeId);

        return theQuery.getSingleResult();
    }
}