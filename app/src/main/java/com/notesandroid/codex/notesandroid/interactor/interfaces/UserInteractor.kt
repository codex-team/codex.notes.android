package com.notesandroid.codex.notesandroid.interactor.interfaces

import com.notesandroid.codex.notesandroid.data.User

interface UserInteractor{
    fun getUser(): User?

    fun saveUser(user: User)

    fun logout()
}