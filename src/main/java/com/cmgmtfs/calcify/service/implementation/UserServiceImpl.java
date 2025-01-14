package com.cmgmtfs.calcify.service.implementation;

import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.dto.UserDTO;
import com.cmgmtfs.calcify.dtomapper.UserDTOMapper;
import com.cmgmtfs.calcify.repository.UserRepository;
import com.cmgmtfs.calcify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository<User> userRepository;
    /**
     * @param user
     */
    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return UserDTOMapper.fromUser(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
//        userRepository.sendVerificationCode(user);
    }
}
