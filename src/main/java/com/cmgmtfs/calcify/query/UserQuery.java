package com.cmgmtfs.calcify.query;

public class UserQuery {
    // jdbc uses 'VALUES (?, ?, ?)' for placeholders
    // however, we need to use the parameters from UserRepositoryImpl here
    public static final String INSERT_USER_QUERY = "INSERT INTO Users (first_name, last_name, email, password) VALUES (:firstName, :lastName, :email, :password)";
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM Users WHERE email = :email";
    public static final String INSERT_ACCOUNT_VERIFICATION_URL_QUERY = "INSERT INTO AccountVerificationUrls (user_id, url) VALUES (:userId, :url)";
}
