package com.codingindia.dailyexpenditure.data.local


import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun isFirstTimeLaunch(): Boolean {
        return sharedPreferences.getBoolean("is_first_time", true)
    }

    fun setFirstTimeLaunchFalse() {
        sharedPreferences.edit { putBoolean("is_first_time", false) }
    }

    fun saveUserName(name: String) {
        sharedPreferences.edit { putString("user_name", name) }
    }

    fun getUserName(): String {
        return sharedPreferences.getString("user_name", "") ?: ""
    }
}