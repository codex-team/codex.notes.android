package codex.notesandroid.repository

import codex.notesandroid.data.Folder
import codex.notesandroid.data.Note
import io.reactivex.Observable


/**
 * Repository for notes, that can controlled flow data. Getting data from
 * database or api and synced data, if it is needed
 */
interface NoteRepository{
    /**
     * Getting notes by folder id
     *
     */
    fun getNotesInFolder(folderId: Int): Observable<List<Note>>

    fun getFolders(): Observable<List<Folder>>
}