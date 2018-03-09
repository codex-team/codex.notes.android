package com.notesandroid.codex.notesandroid

import android.app.Application
import android.content.ClipData.newIntent
import com.google.gson.GsonBuilder
import com.hawkcatcherkotlin.akscorp.hawkcatcherkotlin.HawkExceptionCatcher
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.Essences.Person
import com.notesandroid.codex.notesandroid.Essences.User
import org.jetbrains.anko.startActivity
import javax.security.auth.login.LoginException

/**
 * Created by AksCorp on 02.02.2018.
 *
 * Application entire point
 */
class ApplicationState : Application() {
    
    /**
     * Global program data
     */
    companion object {
        /**
         * Current user data
         */
        lateinit var currentUser: User
        /**
         * Current user content
         */
        lateinit var currentUserContent: Content
        
        /**
         * Hawk catcher
         */
        lateinit var exceptionCatcher: HawkExceptionCatcher
    }
    
    override fun onCreate() {
        super.onCreate()
        
        exceptionCatcher = HawkExceptionCatcher(this, HAWK_TOKEN)
        try {
            exceptionCatcher.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}