package com.notesandroid.codex.notesandroid.dagger

import com.notesandroid.codex.notesandroid.dagger.modules.AppModule
import com.notesandroid.codex.notesandroid.dagger.modules.UserModule
import com.notesandroid.codex.notesandroid.interactor.UserInteractorImpl
import com.notesandroid.codex.notesandroid.ui.MainActivity
import com.notesandroid.codex.notesandroid.utilities.UserPreferences
import dagger.Component
import javax.inject.Singleton

/**
 * In this class we should declare a function with an argument that points to where to inject our instance or classes
 */
@Component(modules = [AppModule::class, UserModule::class])
@Singleton
interface AppComponent{
    fun inject(mainActivity: MainActivity)
    fun inject(userInteractor: UserInteractorImpl)
    fun inject(userPreferences: UserPreferences)
}