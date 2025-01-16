package com.cmgmtfs.calcify.service.implementation;

import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.dto.UserDTO;
import com.cmgmtfs.calcify.dtomapper.UserDTOMapper;
import com.cmgmtfs.calcify.repository.UserRepository;
import com.cmgmtfs.calcify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cmgmtfs.calcify.dtomapper.UserDTOMapper.fromUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository<User> userRepository;
    /**
     * @param user
     */
    @Override
    public UserDTO createUser(User user) {
        return fromUser(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return fromUser(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        userRepository.sendVerificationCode(user);
    }

    /**
     * @param email 
     * @return
     */
    @Override
    public User getUser(String email) {
        return userRepository.getUserByEmail(email);
    }

    /**
     * @param email 
     * @param code
     * @return
     */
    @Override
    public UserDTO verifyCode(String email, String code) {
        return fromUser(userRepository.verifyCode(email, code));
    }
}
