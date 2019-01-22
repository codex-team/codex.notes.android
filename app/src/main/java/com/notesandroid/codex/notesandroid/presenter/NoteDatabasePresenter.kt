package com.notesandroid.codex.notesandroid.presenter

import com.notesandroid.codex.notesandroid.ApplicationState
import com.notesandroid.codex.notesandroid.database.CodexNotesDatabase
import javax.inject.Inject

/**
 * Created by Shiplayer on 21.10.18.
 */

class NoteDatabasePresenter {
    @Inject
    lateinit var noteDatabase: CodexNotesDatabase

    init {
        ApplicationState.appComponent.inject(this)
    }


}
