package com.notesandroid.codex.notesandroid.dagger

import com.notesandroid.codex.notesandroid.dagger.modules.ApplicationModule
import com.notesandroid.codex.notesandroid.dagger.modules.UserModule
import com.notesandroid.codex.notesandroid.database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.presenter.NoteDatabasePresenter
import com.notesandroid.codex.notesandroid.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * In this class we should declare a function with an argument that points to where to inject our instance or classes
 */
@Component(modules = [ApplicationModule::class, UserModule::class])
@Singleton
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(noteDatabasePresenter: NoteDatabasePresenter)
    fun inject(localDatabaseAPI: LocalDatabaseAPI)
}