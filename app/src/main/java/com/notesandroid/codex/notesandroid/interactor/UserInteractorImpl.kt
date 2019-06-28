package com.notesandroid.codex.notesandroid.interactor

import com.notesandroid.codex.notesandroid.CodexApplication
import com.notesandroid.codex.notesandroid.NotFoundUserException
import com.notesandroid.codex.notesandroid.data.User
import com.notesandroid.codex.notesandroid.database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.interactor.interfaces.UserInteractor
import com.notesandroid.codex.notesandroid.utilities.UserPreferences
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class UserInteractorImpl : UserInteractor {
    @Inject
    lateinit var db: LocalDatabaseAPI

    @Inject
    lateinit var userPreferences: UserPreferences

    val userObservable = BehaviorSubject.create<User>()

    init {
        CodexApplication.appComponent.inject(this)

    }

    override fun getUser(): User? {
        val userId = userPreferences.userId
        if (userId == "") {
            return null
        }
        val token = userPreferences.token
        val profileIcon = userPreferences.profileIcon
        return User(db.getPersonFromDatabase(userId), token, profileIcon)
    }

    override fun saveUser(user: User) {
        userPreferences.userId = user.info!!.id!!
        userPreferences.token = user.jwt!!
        userPreferences.profileIcon = user.profileIconName!!
        userObservable.onNext(user)
    }

    override fun logout() {
        userPreferences.clean()
        userObservable.onError(NotFoundUserException())
    }

    override fun getUserObservable(): Observable<User> {
        return userObservable
    }

}