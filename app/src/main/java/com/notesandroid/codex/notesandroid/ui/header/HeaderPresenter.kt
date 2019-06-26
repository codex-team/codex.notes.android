package com.notesandroid.codex.notesandroid.ui.header

import com.notesandroid.codex.notesandroid.base.BasePresenter
import com.notesandroid.codex.notesandroid.data.User
import com.notesandroid.codex.notesandroid.interactor.interfaces.UserInteractor
import javax.inject.Inject

class HeaderPresenter : BasePresenter<HeaderView> {

    @Inject
    lateinit var userInteractor: UserInteractor

    override var view: HeaderView? = null

    override fun attachView(view: HeaderView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    fun checkIfLogin(): Boolean {
        return getUser() != null
    }

    fun getUser(): User? {
        return userInteractor.getUser()
    }
}