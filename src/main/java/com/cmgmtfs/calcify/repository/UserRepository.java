package com.cmgmtfs.calcify.repository;

import com.cmgmtfs.calcify.domain.User;

import java.util.Collection;


public interface UserRepository <T extends User>{
    /* Basic CRUD operations */
    // create
    T create(T data);

    // read
    // read many
    Collection<T> list(int page, int pageSize);
    // read one
    T get(Long id);

    // update
    T update(T data);

    // delete
    // returns a boolean so that we know if successful or not
    boolean delete(Long id);

    /* More Complex Operations */
    User getUserByEmail(String email);
}
