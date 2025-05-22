package com.unimib.koby.util;

/**
 * Utility class to save constants used by the app or for the api
 */

public class Constants {

    // Constants for SharedPreferences
    public static final String SHARED_PREFERENCES_FILE_NAME = "com.unimib.koby.preferences";

    // Constants for EncryptedSharedPreferences
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "com.unimib.koby.encrypted_preferences";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PASSWORD = "password";
    public static final String ID_TOKEN = "google_token";

    public static final String SHARED_PREFERENCES_FIRST_LOADING = "first_loading";

    // Constants for encrypted files
    public static final String ENCRYPTED_DATA_FILE_NAME = "com.unimib.koby.encrypted_file.txt";

    // Constants for Room database
    public static final String KOBY_DATABASE_NAME = "koby_db";
    public static final int DATABASE_VERSION = 1;

    // Constants for OpenAi Api
    public static final String KOBY_API_BASE_URL = "https://openai.com"; //Da modificare
    public static final String KOBY_API_TERM = "study_space";
    public static final int FRESH_TIMEOUT = 3600000; // 1 hour in milliseconds
    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";
    public static final int MINIMUM_PASSWORD_LENGTH = 6;

    // Constants for Firebase Realtime Database
    public static final String FIREBASE_REALTIME_DATABASE = "https://koby-a89c5-default-rtdb.europe-west1.firebasedatabase.app/"; //Da modificare
    public static final String FIREBASE_USERS_COLLECTION = "users";
}
