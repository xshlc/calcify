####################################################################
###                                                             ####
### Author: Choi                                                ####
### Date: January 4th, 2025                                     ####
### Version: 1.0                                                ####
###                                                             ####
####################################################################

/*
 * --- General Rules ---
 * Use underscore_names instead of camelCase
 * Table names should be plural
 * Spell out id fields (item_id instead of id)
 * Don't use ambiguous column names
 * Name foreign key columns the same as the columns they refer to
 * Use caps for all SQL queries
 */

CREATE SCHEMA IF NOT EXISTS calcifydb;

SET NAMES 'UTF8MB4';
SET TIME_ZONE = 'US/Eastern';
SET TIME_ZONE = '-4'; # timezone offset

USE calcifydb;

###### ===== USERS ===== ######
DROP TABLE IF EXISTS Users;

CREATE TABLE Users
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50)     NOT NULL,
    last_name  VARCHAR(50)     NOT NULL,
    email      VARCHAR(100)    NOT NULL,
    password   VARCHAR(255) DEFAULT NULL,
    address    VARCHAR(255) DEFAULT NULL,
    phone      VARCHAR(30)  DEFAULT NULL,
    title      VARCHAR(50)  DEFAULT NULL,
    bio        VARCHAR(255) DEFAULT NULL,
    enabled    BOOLEAN      DEFAULT FALSE,
    non_locked BOOLEAN      DEFAULT TRUE,
    using_mfa  BOOLEAN      DEFAULT FALSE,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    image_url  VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/17690/17690805.png',
    CONSTRAINT UQ_Users_Email UNIQUE (email)
);

# teal icon
# https://cdn-icons-png.flaticon.com/512/17690/17690805.png

# bright red icon
# https://cdn-icons-png.flaticon.com/512/10307/10307911.png

###### ===== ROLES ===== ######
DROP TABLE IF EXISTS Roles;

CREATE TABLE Roles
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50)     NOT NULL,
    permission VARCHAR(255)    NOT NULL, -- user:read, user:delete, customer:read
    CONSTRAINT UQ_Roles_Name UNIQUE (name)
);

###### ===== USER ROLES ===== ######
DROP TABLE IF EXISTS UserRoles;

CREATE TABLE UserRoles
(
    id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Roles (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT UQ_UserRoles_User_Id UNIQUE (user_id),
    CONSTRAINT UQ_UserRoles_Role_Id UNIQUE (role_id)
);