package com.cmgmtfs.calcify.repository.implementation;

import com.cmgmtfs.calcify.domain.Role;
import com.cmgmtfs.calcify.domain.User;
import com.cmgmtfs.calcify.domain.UserPrincipal;
import com.cmgmtfs.calcify.dto.UserDTO;
import com.cmgmtfs.calcify.exception.ApiException;
import com.cmgmtfs.calcify.repository.RoleRepository;
import com.cmgmtfs.calcify.repository.UserRepository;
import com.cmgmtfs.calcify.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.cmgmtfs.calcify.enumeration.RoleType.ROLE_USER;
import static com.cmgmtfs.calcify.enumeration.VerificationType.ACCOUNT;
import static com.cmgmtfs.calcify.query.UserQuery.*;
import static com.cmgmtfs.calcify.utils.SmsUtils.sendSMS;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl<T extends User> implements UserRepository<T>, UserDetailsService {
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    /**
     * @param user
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
            user.setId(requireNonNull(holder.getKey())
                    .longValue());
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());// Send verification URL
            String verificationUrl = getVerificationUrl(UUID.randomUUID()
                    .toString(), ACCOUNT.getType());
            jdbcTemplate.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY,
                    of("userId", user.getId(), "url", verificationUrl));
            user.setEnabled(false);
            user.setNotLocked(true);
            return user;
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
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
        return null;
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

    // // Additional methods // //

    /**
     * @param email
     * @return
     */
    private Integer getEmailCount(String email) {
        return jdbcTemplate.queryForObject(COUNT_USER_EMAIL_QUERY, of("email", email), Integer.class);
    }

    /**
     * @param user
     * @return
     */
    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource().addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    /**
     * @param key
     * @param type
     * @return
     */
    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/user/verify/" + type + "/" + key)
                .toUriString();
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param email the email identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user,
                    roleRepository.getRoleByUserId(user.getId())
                            .getPermission());
//            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject(SELECT_USER_BY_EMAIL_QUERY,
                    of("email", email),
                    new UserRowMapper());
            System.out.println("Fetching user by userRepo: " + user);
            return user;
        } catch (EmptyResultDataAccessException exception) {
//            log.error(exception.getMessage());
            throw new ApiException("No User found by email: " + email);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * @param user
     */
    @Override
    public void sendVerificationCode(UserDTO user) {
        // date + 1 ... which means valid for 24 hours
        String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);

        String verificationCode = randomAlphabetic(8).toUpperCase();

        try {
            jdbcTemplate.update(DELETE_VERIFICATION_CODE_BY_USER_ID,
                    of("id", user.getId()));
            jdbcTemplate.update(INSERT_VERIFICATION_CODE_QUERY,
                    of("userId", user.getId(), "code", verificationCode, "expirationDate", expirationDate));
            // !!! COMMENT THIS OUT in production and testing because every text costs money $$$
            // sending the text ...
//            sendSMS(user.getPhone(), "From: Calcify \nVerification Code\n" + verificationCode);
            log.info("Verification Code: {}", verificationCode);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * @param email
     * @param code
     * @return
     */
    @Override
    public User verifyCode(String email, String code) {
        // if the code is expired, throw an exception
        if(isVerificationCodeExpired(code)) throw new ApiException("Verification code expired. Please login again.");
        try {
            User userByCode = jdbcTemplate.queryForObject(SELECT_USER_BY_USER_CODE_QUERY, of("code", code), new UserRowMapper());
            User userByEmail = jdbcTemplate.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
            // check if the user found by using the code is the same as the user found by the email
            // otherwise, anyone with the code can be given an access token to login
            if (userByCode.getEmail().equalsIgnoreCase(userByEmail.getEmail())) {
                // if they are the same, then return any of the User objects as they are the same
                jdbcTemplate.update(DELETE_CODE, of("code", code));
                return userByCode;
            } else {
                throw new ApiException("Code is invalid. Please try again.");
            }
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("Unable to find record.");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    private Boolean isVerificationCodeExpired(String code) {
        try {
            return jdbcTemplate.queryForObject(SELECT_CODE_EXPIRATION_QUERY, of("code", code), Boolean.class);
        } catch (ApiException exception) {
            throw new ApiException("This code is not valid. Please try again.");
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }
}
