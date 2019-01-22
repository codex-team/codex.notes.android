package com.notesandroid.codex.notesandroid.loaders

import android.content.Context
import androidx.loader.content.Loader
import com.notesandroid.codex.notesandroid.presenter.NoteDatabasePresenter

class NoteDatabaseLoader(context: Context) : Loader<NoteDatabasePresenter>(context){
    var presenter: NoteDatabasePresenter? = null
    override fun onStartLoading() {
        super.onStartLoading()
        if(presenter == null){
            forceLoad()
        } else{
            deliverResult(presenter)
        }
    }

    override fun forceLoad() {
        presenter = NoteDatabasePresenter()
        deliverResult(presenter)
    }
}
