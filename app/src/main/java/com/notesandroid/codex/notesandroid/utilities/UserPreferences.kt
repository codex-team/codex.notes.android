package com.notesandroid.codex.notesandroid.utilities

import android.content.SharedPreferences
import com.notesandroid.codex.notesandroid.CodexApplication
import com.notesandroid.codex.notesandroid.database.share.UserData
import javax.inject.Inject

class UserPreferences {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    init {
        CodexApplication.appComponent.inject(this)
    }

    public var userId = ""
        get() = sharedPreferences.getString(UserData.FIELDS.LAST_USER_ID, "") ?: ""
        set(value) {
            field = value
            sharedPreferences.edit().putString(UserData.FIELDS.LAST_USER_ID, value).apply()
        }
    public var token = ""
        get() = sharedPreferences.getString(UserData.FIELDS.LAST_USER_TOKEN, "") ?: ""
        set(value) {
            field = value
            sharedPreferences.edit().putString(UserData.FIELDS.LAST_USER_TOKEN, value).apply()
        }
    public var profileIcon = ""
        get() = sharedPreferences.getString(UserData.FIELDS.PROFILE_ICON, "") ?: ""
        set(value) {
            field = value
            sharedPreferences.edit().putString(UserData.FIELDS.PROFILE_ICON, value).apply()
        }

    fun clean() {
        sharedPreferences.edit()
            .remove(UserData.FIELDS.LAST_USER_ID)
            .remove(UserData.FIELDS.LAST_USER_TOKEN)
            .remove(UserData.FIELDS.PROFILE_ICON)
            .apply()
    }
}