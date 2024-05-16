package com.javabot.serviceimp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.javabot.models.Employee;
import com.javabot.service.EmployeeService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    private EntityManager entityManager;

    public EmployeeServiceImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }


    @Override
    public Employee findById(Integer id) {
        return entityManager.find(Employee.class, id);
    }
    
    @Override
    @Transactional
    public void create(Employee theEmployee) {
        entityManager.persist(theEmployee);
    }
    
    @Override
    @Transactional
    public void update(Employee theEmployee) {
        entityManager.merge(theEmployee);
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        Employee theEmployee = entityManager.find(Employee.class, id);
        entityManager.remove(theEmployee);
    }
     
    @Override
    public Employee findByTelegramId(long telegramId){
        String sqlQuery = "SELECT e FROM Employee e WHERE e.telegramId = :telegramId";
        TypedQuery<Employee> theQuery = entityManager.createQuery(sqlQuery, Employee.class);
        theQuery.setParameter("telegramId", telegramId);
        return theQuery.getSingleResult();
    }

    @Override
    public List<Employee> findAll() {
        String sqlQuery = "SELECT e FROM Employee e";
        TypedQuery<Employee> theQuery = entityManager.createQuery(sqlQuery, Employee.class);
        return theQuery.getResultList();
    }
}
