package com.javabot.serviceimp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.javabot.models.Employee;
import com.javabot.models.Manager;
import com.javabot.models.Team;
import com.javabot.service.TeamService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Service
public class TeamServiceImpl implements TeamService{

    private EntityManager entityManager;

    public TeamServiceImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public Team findById(Integer id) {
        return entityManager.find(Team.class, id);
    }

    @Override
    @Transactional
    public void create(Team theTeam) {
        entityManager.persist(theTeam);
    }

    @Override
    @Transactional
    public void update(Team theTeam) {
        entityManager.merge(theTeam);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Team theTeam = entityManager.find(Team.class, id);
        entityManager.remove(theTeam);
    }
    @Override
    @Transactional
    public List<Employee> teamEmployees(Integer id) {
        TypedQuery<Employee> theQuery = entityManager.createQuery("SELECT e FROM Employee e WHERE e.team = :teamId", Employee.class);
        theQuery.setParameter("teamId", id);
        return theQuery.getResultList();
    }
    @Override
    @Transactional
    public Manager teamManager(Integer id){
        TypedQuery<Manager> theQuery = entityManager.createQuery("SELECT t.manager FROM Team t WHERE t.id = :teamId", Manager.class);
        theQuery.setParameter("teamId", id);
        return theQuery.getSingleResult();
    }

    @Transactional
    public List<Team> getAllTeams(){
        TypedQuery<Team> theQuery = entityManager.createQuery("SELECT t FROM Team t", Team.class);
        return theQuery.getResultList();
    }

}
