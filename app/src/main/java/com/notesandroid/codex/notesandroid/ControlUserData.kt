package com.notesandroid.codex.notesandroid

import android.content.Context
import com.auth0.android.jwt.JWT
import com.notesandroid.codex.notesandroid.Database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.Essences.Person
import com.notesandroid.codex.notesandroid.SharedPreferenceDatabase.UserData

/**
 * Created by AksCorp on 08.03.2018.
 *
 * Control user data (get/put in local database)
 */
class ControlUserData(val context: Context) {
    
    /**
     * Put information from custom jwt token to database and shared preference
     *
     * @param jwt decoded jwt object
     * @param token raw jwt token
     */
    fun initUserInformation(jwt: JWT, token: String) {
        
        val userId = jwt.getClaim("user_id").asString()
        val name = jwt.getClaim("name").asString()
        val email = jwt.getClaim("email").asString()
        val person = Person(userId, name, email)
        
        val db = LocalDatabaseAPI(context)
        
        if (db.isPersonExistInDatabase(person!!))
            db.updatePersonInDatabase(person!!)
        else
            db.insertPersonInDatabase(person)
        
        val prefs = context.getSharedPreferences(UserData.NAME, 0)
        prefs.edit().putString(UserData.FIELDS.LAST_USER_TOKEN, token).apply()
        prefs.edit().putString(UserData.FIELDS.LAST_USER_ID, userId).apply()
    }
}