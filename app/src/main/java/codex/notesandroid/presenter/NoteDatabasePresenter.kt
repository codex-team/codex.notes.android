package codex.notesandroid.presenter

import codex.notesandroid.ApplicationState
import codex.notesandroid.database.CodexNotesDatabase
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
