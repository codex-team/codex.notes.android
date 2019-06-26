package com.notesandroid.codex.notesandroid.interactor

import com.notesandroid.codex.notesandroid.CodexApplication
import com.notesandroid.codex.notesandroid.data.User
import com.notesandroid.codex.notesandroid.database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.interactor.interfaces.UserInteractor
import com.notesandroid.codex.notesandroid.utilities.UserPreferences
import javax.inject.Inject

class UserInteractorImpl : UserInteractor{

    @Inject
    lateinit var db:LocalDatabaseAPI

    @Inject
    lateinit var userPreferences: UserPreferences

    init {
        CodexApplication.appComponent.inject(this)
    }

    override fun getUser(): User? {
        val userId = userPreferences.userId
        if(userId == ""){
            return null
        }
        val token = userPreferences.token
        val profileIcon = userPreferences.profileIcon
        return User(db.getPersonFromDatabase(userId), token, profileIcon)
    }

    override fun saveUser(user: User){
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun logout() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}