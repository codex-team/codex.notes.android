package com.notesandroid.codex.notesandroid

import android.app.Application
import com.hawkcatcherkotlin.akscorp.hawkcatcherkotlin.HawkExceptionCatcher

/**
 * Created by AksCorp on 02.02.2018.
 *
 * Application entire point
 */
class ApplicationState : Application() {

    companion object {
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
        } catch (
          e: Exception
        ) {
            e.printStackTrace()
        }
    }
}