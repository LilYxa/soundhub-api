package com.soundhub.api;

public class Constants {
    public static final String USER_RESOURCE_NAME = "User";

    public static final String POST_RESOURCE_NAME = "Post";

    public static final String CHAT_RESOURCE_NAME = "Chat";
    public static final String MESSAGE_RESOURCE_NAME = "Message";

    public static final String USERNAME_FIELD = "username";
    public static final String EMAIL_FIELD = "email";
    public static final String ID_FIELD = "id";

    public static final String USER_EMAIL_EXISTS_MSG = "This email is already taken";
    public static final String USER_USERNAME_EXISTS_MSG = "This username is already taken";
    public static final String USER_ALREADY_EXISTS = "This user already exists";
    public static final String USERNAME_NOT_FOUND = "User not found with this username or email: %s";
    public static final String SUCCESSFUL_LOGOUT = "You have successfully logged out";
    public static final String INVALID_EMAIL_PASSWORD = "Invalid email or password";
    public static final String FILE_ALREADY_EXISTS = "This file already exist! Please enter another file name.";
    public static final String PERMISSION_MESSAGE = "You don't have permissions for this operation.";
    public static final String CHAT_DELETE_SUCCESS_MSG = "Chat with id: %s was successfully deleted";
    public static final String CHAT_NOT_CONTAINS_USER = "This user is not related to this chat";
    public static final String UNAUTHORIZED_ERROR_MESSAGE = "Sorry, You're not authorized to access this resource.";

    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String BLACKLIST_KEY_PREFIX = "blacklist:";
    public static final String BLACKLIST_CACHE_NAME = "jwt-blacklist";
    public static final String FILE_PATH_PART = "/api/v1/files/";
    public static final String DEFAULT_USER_AVATAR_NAME = "default.jpg";
}
