package com.cmgmtfs.calcify.service.implementation;

import com.cmgmtfs.calcify.domain.Role;
import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.dto.UserDTO;
import com.cmgmtfs.calcify.repository.RoleRepository;
import com.cmgmtfs.calcify.repository.UserRepository;
import com.cmgmtfs.calcify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cmgmtfs.calcify.dtomapper.UserDTOMapper.fromUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;
    /**
     * @param user
     */
    @Override
    public UserDTO createUser(User user) {
        return mapToUserDTO(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        System.out.println("Fetching user by email (UserServiceImpl): " + email);
        return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return mapToUserDTO(userRepository.get(userId));
    }


    @Override
    public void sendVerificationCode(UserDTO user) {
        userRepository.sendVerificationCode(user);
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return mapToUserDTO(userRepository.verifyCode(email, code));
    }

    private UserDTO mapToUserDTO(User user) {
        return fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
