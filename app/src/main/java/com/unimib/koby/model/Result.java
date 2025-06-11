package com.unimib.koby.model;

public abstract class Result {

    public boolean isSuccess() { return this instanceof Success; }

    public static final class Success<T> extends Result {
        private final T data;
        public Success(T data) { this.data = data; }
        public T getData() { return data; }
    }

    public static final class Error extends Result {
        private final String message;
        public Error(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}