package com.unimib.koby.util;

/**
 * Utility class to save constants used by the app or for the api
 */

public class Constants { //Not Used

    // Constants for SharedPreferences
    public static final String SHARED_PREFERENCES_FILE_NAME = "com.unimib.koby.preferences";

    // Constants for EncryptedSharedPreferences
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "com.unimib.koby.encrypted_preferences";

    // Constants for encrypted files
    public static final String ENCRYPTED_DATA_FILE_NAME = "com.unimib.koby.encrypted_file.txt";

    // Constants for Room database
    public static final String KOBY_DATABASE_NAME = "koby_db";
    public static final int DATABASE_VERSION = 1;

    // Constants for OpenAi Api
    public static final String KOBY_API_BASE_URL = "https://openai.com"; //Da modificare
    public static final String KOBY_API_TERM = "study_space";
    public static final int FRESH_TIMEOUT = 3600000; // 1 hour in milliseconds

    // Constants for Firebase Realtime Database
    public static final String FIREBASE_REALTIME_DATABASE = "https://koby-a89c5-default-rtdb.europe-west1.firebasedatabase.app/"; //Da modificare
    public static final String FIREBASE_USERS_COLLECTION = "users";
}
