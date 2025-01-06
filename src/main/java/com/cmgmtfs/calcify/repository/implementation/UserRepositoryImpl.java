package com.cmgmtfs.calcify.repository.implementation;

import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.exception.ApiException;
import com.cmgmtfs.calcify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl<T extends User> implements UserRepository<T> {
    private static final String COUNT_USER_EMAIL_QUERY = "";
    private static final String INSERT_USER_QUERY = "";
    public final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * @param data 
     * @return
     */
    @Override
    public User create(User user) {
        // Check whether the email is unique
        if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0) throw new ApiException("Email already in use. Please use a different email and try again."));
        // Save new user
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameters = getSqlParameterSource(user);
            jdbcTemplate.update(INSERT_USER_QUERY, parameters, holder);
            // requireNonNull is a static import
            user.setId(requireNonNull(holder.getKey())
                    .longValue());
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
        } catch (EmptyResultDataAccessException exception) {} catch (Exception exception) {}
        // Add role to the user
        // Send verification URL
        // Save URL and verification table
        // Send email to user with verification URL
        // Return the newly created user
        // If any errors, throw exception with proper message
        return null;
    }


    /**
     * @param page 
     * @param pageSize
     * @return
     */
    @Override
    public Collection<T> list(int page, int pageSize) {
        return List.of();
    }

    /**
     * @param id 
     * @return
     */
    @Override
    public T get(Long id) {
        return null;
    }

    /**
     * @param data 
     * @return
     */
    @Override
    public T update(T data) {
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

    // Other methods
    private Integer getEmailCount(String email) {
        return jdbcTemplate.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email",email), Integer.class);
    }
    private SqlParameterSource getSqlParameterSource(User user) {
    }
}
