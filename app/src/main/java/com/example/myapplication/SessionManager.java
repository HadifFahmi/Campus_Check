package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    // SharedPreferences Key for storing session data
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";

    private static final String KEY_NAME = "name";
    private static final String KEY_ROLE = "role";

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save student ID to SharedPreferences
    public void createSession(String Id, String User, String Name, int Role){
        editor.putString(KEY_ID, Id);
        editor.putString(KEY_USERNAME, User);
        editor.putString(KEY_NAME, Name);
        editor.putInt(KEY_ROLE, Role);
        editor.apply();  // Apply changes asynchronously
    }

    // Retrieve student ID from SharedPreferences
    public String getId() {
        return sharedPreferences.getString(KEY_ID, null);  // Returns null if not found
    }

    // Retrieve username from SharedPreferences
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);  // Returns null if not found
    }

    public String getName() {
        return sharedPreferences.getString(KEY_NAME, null);  // Returns null if not found
    }

    public int getRole() {
        return sharedPreferences.getInt(KEY_ROLE, 0);  // Returns 0 if not found
    }

    // Clear session data (e.g., when user logs out)
    public void logout() {
        editor.clear();
        editor.apply();
    }

    // Check if session exists
    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_ID);
    }
}