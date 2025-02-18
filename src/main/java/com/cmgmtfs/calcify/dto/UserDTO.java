package com.cmgmtfs.calcify.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    //
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    // we want to send everything BUT the password field
    private String address;
    private String phone;
    private String title;
    private String bio;
    private String imageUrl;
    private boolean enabled;
    private boolean isNotLocked;
    private boolean isUsingMfa; // multi-factor authentication
    private LocalDateTime createdAt;
    private String roleName;
    private String permissions;
}
