package com.notesandroid.codex.notesandroid.dagger.modules

import android.content.Context
import com.notesandroid.codex.notesandroid.database.CodexNotesDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Class with implemented a function that return instance of class that should be injected
 */
@Module
class ApplicationModule(private val applicationContext: Context) {


    /**
     * Function that provide us application context
     * @return application context
     */
    @Provides
    @Singleton
    fun getApplicationContext() = applicationContext

    /**
     * Function that provide instance of database
     */
    @Provides
    @Singleton
    fun getDatabase() = CodexNotesDatabase(applicationContext)
}