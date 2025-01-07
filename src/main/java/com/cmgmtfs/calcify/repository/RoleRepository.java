package com.cmgmtfs.calcify.repository;

import com.cmgmtfs.calcify.domain.Role;

import java.util.Collection;

public interface RoleRepository <T extends Role> {
    /* Basic CRUD operations */
    T create(T data);

    // read
    // read many
    Collection<T> list(int page, int pageSize);
    // read one
    T get(Long id);

    T update(T data);

    // returns a boolean so that we know if successful or not
    boolean delete(Long id);

    /* More complex operations */
    void addRoleToUser(Long userId, String roleName);

    Role getRoleByUserId(Long userId);
    Role getRoleByUserEmail(String email);
    void updateUserRole(Long userId, String roleName);



}
