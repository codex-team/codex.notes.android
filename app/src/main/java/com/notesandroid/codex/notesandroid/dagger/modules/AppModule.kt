package com.notesandroid.codex.notesandroid.dagger.modules

import android.content.Context
import com.notesandroid.codex.notesandroid.database.LocalDatabaseAPI
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Class with implemented a function that return instance of class that should be injected
 */
@Module
class AppModule(private val applicationContext: Context){


    /**
     * Function that provide us application context
     * @return application context
     */
    @Provides
    @Singleton
    fun getApplicationContext() = applicationContext

    @Provides
    @Singleton
    fun getLocaleDatabase(context: Context) = LocalDatabaseAPI(context)

    @Provides
    @Singleton
    fun getSharedPreferences() = applicationContext.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
}