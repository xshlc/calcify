package com.cmgmtfs.calcify.service;

import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.dto.UserDTO;

public interface UserService {
    UserDTO createUser(User user);
    UserDTO getUserByEmail(String email);
    void sendVerificationCode(UserDTO user);

//    User getUser(String email);

    UserDTO verifyCode(String email, String code);

    // testing user profile not working
    UserDTO getUserById(Long userId);
}
