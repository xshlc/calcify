package com.cmgmtfs.calcify.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final User user;
    private final String permissions;
//    private final Role role;
    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of();
        // permissions string:
        // USER:READ, CUSTOMER:READ, etc.
        // this will split the string and save the result in an array using stream
        // SimpleGrantedAuthority... we can accept anything that extends GrantedAuthority
        return stream(permissions.split(",".trim())).map(SimpleGrantedAuthority::new).collect(toList());
//        return AuthorityUtils.commaSeparatedStringToAuthorityList(role.getPermission());
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    @Override
    public String getUsername() {
        // in this case, we're dealing with the email and not the username
        return this.user.getEmail();
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        // we're not using this
        // .. so we want this to always return true
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.user.isNotLocked();
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        // we're not using this
        // .. so we want this to always return true
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    @Override
    public boolean isEnabled() {
//        return this.user.isEnabled();
        return true;
    }
}
