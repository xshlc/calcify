package com.cmgmtfs.calcify.repository;

import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.dto.UserDTO;

import java.util.Collection;


public interface UserRepository<T extends User> {
    /* Basic CRUD Operations */
    T create(T data);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);

    /* More Complex Operations */
    User getUserByEmail(String email);
    void sendVerificationCode(UserDTO user);
    User verifyCode(String email, String code);
}
