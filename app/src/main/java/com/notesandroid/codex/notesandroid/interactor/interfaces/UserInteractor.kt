package com.notesandroid.codex.notesandroid.interactor.interfaces

import com.notesandroid.codex.notesandroid.data.User
import io.reactivex.Observable

interface UserInteractor{
    fun getUser(): User?

    fun saveUser(user: User)

    fun logout()

    fun getUserObservable(): Observable<User>
}