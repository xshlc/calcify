package com.cmgmtfs.calcify.repository.implementation;

import com.cmgmtfs.calcify.domain.Role;
import com.cmgmtfs.calcify.exception.ApiException;
import com.cmgmtfs.calcify.repository.RoleRepository;
import com.cmgmtfs.calcify.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.cmgmtfs.calcify.query.RoleQuery.INSERT_ROLE_TO_USER;
import static com.cmgmtfs.calcify.query.RoleQuery.SELECT_ROLE_BY_NAME_QUERY;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {


    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param data
     * @return
     */
    @Override
    public Role create(Role data) {
        return null;
    }

    /**
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Collection<Role> list(int page, int pageSize) {
        return List.of();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Role get(Long id) {
        return null;
    }

    /**
     * @param data
     * @return
     */
    @Override
    public Role update(Role data) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public boolean delete(Long id) {
        return false;
    }

    /**
     * @param userId
     * @param roleName
     */
    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("Adding role {} to user {}", roleName, userId);
        try {
            // static import of Map.of()
            Role role = jdbcTemplate.queryForObject(SELECT_ROLE_BY_NAME_QUERY,
                    of("roleName", roleName),
                    new RoleRowMapper());
            // we get a warning for 'role.getId()', this is because we have to do require not null
            // static import for Objects.requireNonNull()
            jdbcTemplate.update(INSERT_ROLE_TO_USER, of("userId", userId, "roleId", requireNonNull(role)
                    .getId()));
        }
        // this catch block is redundant because it does the same in the UserRepositoryImpl create() 
//        catch (EmptyResultDataAccessException exception) {
//            // the only operation that can cause this exception is the roleRepository.addRoleToUser() operation
//            throw new ApiException("No role found by name: " + ROLE_USER.name());
//        }
        catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }

    }

    /**
     * @param userId
     * @return
     */
    @Override
    public Role getRoleByUserId(Long userId) {
        return null;
    }

    /**
     * @param email
     * @return
     */
    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    /**
     * @param userId
     * @param roleName
     */
    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}
