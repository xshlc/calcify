package com.cmgmtfs.calcify.repository.implementation;

import com.cmgmtfs.calcify.domain.Role;
import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.exception.ApiException;
import com.cmgmtfs.calcify.repository.RoleRepository;
import com.cmgmtfs.calcify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.cmgmtfs.calcify.enumeration.RoleType.ROLE_USER;
import static com.cmgmtfs.calcify.enumeration.VerificationType.ACCOUNT;
import static com.cmgmtfs.calcify.query.UserQuery.*;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl<T extends User> implements UserRepository<T> {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    /**
     * @param data
     * @return
     */
    @Override
    public User create(User user) {
        // Check whether the email is unique
        if (getEmailCount(user.getEmail()
                .trim()
                .toLowerCase()) > 0)
            throw new ApiException("Email already in use. Please use a different email and try again.");
        // Save new user
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameters = getSqlParameterSource(user);
            jdbcTemplate.update(INSERT_USER_QUERY, parameters, holder);
            // requireNonNull is a static import
            user.setId(requireNonNull(holder.getKey())
                    .longValue());
            // Add role to the user
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());// Send verification URL
            // Save verification URL
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(),ACCOUNT.getType());
            // Save URL and verification table
            // static import for Map.of()
            jdbcTemplate.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, of("userId", user.getId(),"url", verificationUrl));
            // Send email to user with verification URL
            // will create EmailService later
            //emailService.sendVerification(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
            user.setEnabled(false);
            user.setNotLocked(true);
            // Return the newly created user
            return user;
            // If any errors, throw exception with proper message
        } catch (EmptyResultDataAccessException exception) {
            // the only operation that can cause this exception is the roleRepository.addRoleToUser() operation
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }

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
        return jdbcTemplate.queryForObject(COUNT_USER_EMAIL_QUERY, of("email", email), Integer.class);
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource().addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/"+ type + "/" + key).toUriString();
    }
}
