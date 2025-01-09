package com.cmgmtfs.calcify.query;

public class RoleQuery {
    public static final String INSERT_ROLE_TO_USER = "INSERT INTO UserRoles (user_id, role_id) VALUES (:userId, :roleId)";
    public static final String SELECT_ROLE_BY_NAME_QUERY = "SELECT * FROM Roles WHERE name = :name";
}
