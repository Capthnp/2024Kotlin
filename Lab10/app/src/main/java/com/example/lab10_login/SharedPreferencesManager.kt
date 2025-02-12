package com.example.lab10_login

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferencesManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = preferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = preferences.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userId: String?
        get() = preferences.getString(KEY_USER_ID, null)
        set(value) = preferences.edit().putString(KEY_USER_ID, value).apply()

    fun clearUserAll() {
        preferences.edit { clear() }
    }

    fun clearUserLogin() {
        preferences.edit { putBoolean(KEY_IS_LOGGED_IN, false) }
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
    }
}
