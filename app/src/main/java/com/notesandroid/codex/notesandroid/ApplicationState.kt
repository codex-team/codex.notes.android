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
 */
class ApplicationState : Application() {

    companion object {
        const val DEBUG = false
        lateinit var currentUser: User
        lateinit var currentUserContent: Content
        lateinit var exceptionCatcher: HawkExceptionCatcher

    }
    val HAWK_TOKEN = "8e478c91-e278-402a-857d-c4e278afff92"

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