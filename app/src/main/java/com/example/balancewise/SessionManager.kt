package com.example.balancewise

import android.content.Context

object SessionManager {
    private const val PREF_NAME = "balancewise_prefs"
    private const val KEY_USER_ID = "user_id"

    fun saveUserId(context: Context, userId: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(context: Context): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_USER_ID, -1)
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}