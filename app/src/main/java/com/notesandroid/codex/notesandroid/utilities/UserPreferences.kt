package com.notesandroid.codex.notesandroid.utilities

import android.content.SharedPreferences
import com.notesandroid.codex.notesandroid.CodexApplication
import com.notesandroid.codex.notesandroid.database.share.UserData
import javax.inject.Inject

class UserPreferences {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    init{
        CodexApplication.appComponent.inject(this)
    }

    public val userId
        get() = sharedPreferences.getString(UserData.FIELDS.LAST_USER_ID, "") ?: ""
    public val token
        get() = sharedPreferences.getString(UserData.FIELDS.LAST_USER_TOKEN, "") ?: ""
    public val profileIcon
        get() = sharedPreferences.getString(UserData.FIELDS.PROFILE_ICON, "") ?: ""
}