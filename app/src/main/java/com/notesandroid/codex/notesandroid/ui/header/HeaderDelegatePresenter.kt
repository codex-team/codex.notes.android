package com.notesandroid.codex.notesandroid.ui.header

import com.notesandroid.codex.notesandroid.NotFoundUserException
import com.notesandroid.codex.notesandroid.base.BasePresenter
import com.notesandroid.codex.notesandroid.data.User
import com.notesandroid.codex.notesandroid.interactor.interfaces.UserInteractor
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import kotlin.properties.Delegates

class HeaderDelegatePresenter : BasePresenter<HeaderView> {

    @Inject
    lateinit var userInteractor: UserInteractor

    private var lastStateOfLogin: User? by Delegates.observable<User?>(null) { param, old, new ->
        action()
    }
    private val disposable: Disposable

    private var action: () -> Unit = {}

    fun connectToDelegate(action: () -> Unit){
        this.action = action
    }

    init {
        disposable = userInteractor.getUserObservable().onErrorResumeNext { t: Throwable ->
            if (t is NotFoundUserException){
                Observable.just(null)
            } else
                Observable.error(t)
        }.subscribe{
            lastStateOfLogin = it
        }
    }

    fun getStatusOfLogin() = lastStateOfLogin

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