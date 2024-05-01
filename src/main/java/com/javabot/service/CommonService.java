package com.javabot.service;

public interface CommonService<T> {

    T findById(Integer id);

    void create(T entity);

    void update(T entity);

    void delete(Integer id);
}
