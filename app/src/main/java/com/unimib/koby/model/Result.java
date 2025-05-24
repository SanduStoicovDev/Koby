package com.unimib.koby.model;

public abstract class Result {
    /** Se l’operazione è andata a buon fine. */
    public boolean isSuccess() { return this instanceof UserResponseSuccess; }

    /** Successo: contiene l’utente autenticato. */
    public static final class UserResponseSuccess extends Result {
        private final User data;
        public UserResponseSuccess(User data) { this.data = data; }
        public User getData() { return data; }
    }

    /** Errore generico con messaggio per lo sviluppatore/UI. */
    public static final class Error extends Result {
        private final String message;
        public Error(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}