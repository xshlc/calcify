package com.cmgmtfs.calcify.service.implementation;

import com.cmgmtfs.calcify.domain.Role;
import com.cmgmtfs.calcify.repository.RoleRepository;
import com.cmgmtfs.calcify.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository<Role> roleRepository;

    /**
     * @param id the user id
     * @return user
     */
    @Override
    public Role getRoleByUserId(Long id) {
        return roleRepository.getRoleByUserId(id);
    }
}
