package codex.notesandroid.interactor

import android.content.Context
import android.util.Log
import codex.notesandroid.database.LocalDatabaseAPI
import codex.notesandroid.data.Content
import codex.notesandroid.data.Folder
import codex.notesandroid.data.Note
import codex.notesandroid.data.Person
import codex.notesandroid.retrofit.CodeXNotesApi
import io.reactivex.Notification
import io.reactivex.Observable

/**
 * Created by Shiplayer on 21.10.18.
 */

/**
 * Interactor for communication between view layer and data.
 */
class NoteInteractor {
    /**
     * Local database api
     */
    private lateinit var sql: LocalDatabaseAPI

    /**
     * Set up local database API for current the interactor
     *
     * [context] used to initialize local database API
     * @see LocalDatabaseAPI
     */
    public fun attachSQL(
      context: Context
    ) {
        sql = LocalDatabaseAPI(context)
    }

    /**
     * For loading person content from sql.
     *
     * @return content that have all information for current user.
     */
    public fun loadPersonContentFromSql(): Content {
        val folders = sql.getFoldersFromDatabase()
        for (folder in folders) {
            val notes = sql.getNotesFromDatabase(folderId = folder.id!!)
            folder.notes = notes
        }

        val content = Content(folders)
        content.rootFolder = content.folders.filter {
            it.isRoot!!
        }.getOrNull(0)

        return content
    }

    /**
     * Get person content using CodeXNotesApi with handle Content.
     *
     * @param userId - using user id after authorization on CodeXNote server.
     * @param jwt - using jwt after authorization on CodeXNote server.
     * @return Observable on Content wrapped in Notification for handling error on low level without
     * breaking the chain.
     * @see CodeXNotesApi
     * @see LocalDatabaseAPI
     */

    public fun getPersonContent(
      userId: String,
      jwt: String
    ): Observable<Notification<Content>> {
        val obs = CodeXNotesApi().getPersonContent(userId, jwt)
        obs.forEach { it ->
            if (it.isOnNext) {
                Log.i(NoteInteractor::class.java.simpleName, "handle from publishObservable")
                for (folder in it.value!!.folders) {
                    handlePerson(folder.owner!!)

                    handleFolders(folder)

                    folder.notes!!.forEach {
                        it.folderId = folder.id
                        handleNote(it)
                    }
                }
            }
        }
        return obs
    }

    /**
     * First remove all notes that have the isRemoved flag set. When check folder contains in database,
     * if true then update it else insert in database
     * @param folder - folder to be processed.
     */

    private fun handleFolders(
      folder: Folder
    ) {
        if (folder.notes != null)
            folder.notes!!.removeAll { it.isRemoved!! }

        if (sql.isFolderExistInDatabase(folder)) {
            sql.updateFolderInDatabase(folder)
        } else sql.insertFolderInDatabase(folder)
    }

    /**
     * Handle this author of the note. When checks this note, exist in database, if true then update
     * him else insert.
     * @param folder - folder to be processed.
     */

    private fun handleNote(
      note: Note
    ) {
        handlePerson(note.author!!)
        if (sql.isNoteExistInDatabase(note)) {
            sql.updateNoteInDatabase(note)
        } else sql.insertNoteInDatabase(note)
    }

    /**
     * Exist this person in the database and insert him if this author don't found in the database.
     */

    private fun handlePerson(
      person: Person
    ) {
        if (!sql.isPersonExistInDatabase(person))
            sql.insertPersonInDatabase(person)
    }
}