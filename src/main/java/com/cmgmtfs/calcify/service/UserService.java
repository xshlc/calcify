package com.cmgmtfs.calcify.service;

import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.dto.UserDTO;

public interface UserService {
    UserDTO createUser(User user);
    UserDTO getUserByEmail(String email);
    void sendVerificationCode(UserDTO user);
}
