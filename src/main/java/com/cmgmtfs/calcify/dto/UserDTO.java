package com.cmgmtfs.calcify.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
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
}
