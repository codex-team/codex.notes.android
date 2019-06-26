package com.notesandroid.codex.notesandroid.base

interface BasePresenter<V: BaseView>{
    var view: V?
    fun attachView(view: V)
    fun detachView()
}