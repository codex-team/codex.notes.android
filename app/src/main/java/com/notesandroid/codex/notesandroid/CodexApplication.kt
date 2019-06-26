package com.notesandroid.codex.notesandroid

import android.app.Application
import com.hawkcatcherkotlin.akscorp.hawkcatcherkotlin.HawkExceptionCatcher
import com.notesandroid.codex.notesandroid.dagger.AppComponent
import com.notesandroid.codex.notesandroid.dagger.DaggerAppComponent
import com.notesandroid.codex.notesandroid.dagger.modules.AppModule

/**
 * Created by AksCorp on 02.02.2018.
 *
 * Application entire point
 */
class CodexApplication : Application() {

    companion object {
        /**
         * Hawk catcher
        */
        lateinit var exceptionCatcher: HawkExceptionCatcher
        lateinit var appComponent: AppComponent

        val component by lazy {

        }
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = buildComponent()
        exceptionCatcher = HawkExceptionCatcher(this, HAWK_TOKEN)
        try {
            exceptionCatcher.start()
        } catch (
          e: Exception
        ) {
            e.printStackTrace()
        }
    }

    private fun buildComponent() = DaggerAppComponent.builder().appModule(AppModule(applicationContext)).build()
}