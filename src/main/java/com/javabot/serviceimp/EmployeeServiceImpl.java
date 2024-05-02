package com.javabot.serviceimp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.javabot.models.Employee;
import com.javabot.models.Task;
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
    public List<Task> allEmployeeTasks(Integer id) {
        TypedQuery<Task> theQuery = entityManager.createQuery("SELECT t FROM Task t WHERE t.employee.id =:employeeId",
        Task.class);
        theQuery.setParameter("employeeId", id);
        return theQuery.getResultList();

    }
}
