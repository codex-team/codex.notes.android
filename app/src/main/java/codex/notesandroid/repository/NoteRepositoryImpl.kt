package codex.notesandroid.repository

import codex.notesandroid.data.Folder
import codex.notesandroid.data.Note
import codex.notesandroid.data.User
import io.reactivex.Observable

class NoteRepositoryImpl(user: User) : NoteRepository{
    override fun getNotesInFolder(folderId: Int): Observable<List<Note>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFolders(): Observable<List<Folder>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}