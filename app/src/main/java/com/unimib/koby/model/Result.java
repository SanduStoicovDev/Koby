package com.unimib.koby.model;

public abstract class Result {
    public boolean isSuccess() { return this instanceof UserResponseSuccess; }

    public static final class UserResponseSuccess extends Result {
        private final User data;
        public UserResponseSuccess(User data) { this.data = data; }
        public User getData() { return data; }
    }

    public static final class Error extends Result {
        private final String message;
        public Error(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}