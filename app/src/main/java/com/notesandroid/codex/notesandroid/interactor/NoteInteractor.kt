package com.notesandroid.codex.notesandroid.interactor

import android.content.Context
import android.util.Log
import com.notesandroid.codex.notesandroid.Database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.Essences.Folder
import com.notesandroid.codex.notesandroid.Essences.Note
import com.notesandroid.codex.notesandroid.Essences.Person
import com.notesandroid.codex.notesandroid.retrofit.CodeXNotesApi
import io.reactivex.Notification
import io.reactivex.Observable

/**
 * Created by Shiplayer on 21.10.18.
 */
class NoteInteractor {

    private lateinit var sql:LocalDatabaseAPI
    //private val publishPersonContent = PublishSubject.create<Content>()

    public fun attachSQL(context: Context){
        sql = LocalDatabaseAPI(context)
    }

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

    public fun getPersonContent(userId:String, jwt:String):Observable<Notification<Content>> {
        val obs = CodeXNotesApi().getPersonContent(userId, jwt)
        obs.forEach{
            if(it.isOnNext) {
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

    private fun handleFolders(folder: Folder){
        if(folder.notes != null)
            folder.notes!!.removeAll{ it.isRemoved!! }

        if(sql.isFolderExistInDatabase(folder)){
            sql.updateFolderInDatabase(folder)
        } else
            sql.insertFolderInDatabase(folder)
    }

    private fun handleNote(note: Note){
        if(!sql.isPersonExistInDatabase(note.author!!)){
            sql.insertPersonInDatabase(note.author!!)
        }
        if(sql.isNoteExistInDatabase(note)){
            sql.updateNoteInDatabase(note)
        } else
            sql.insertNoteInDatabase(note)
    }

    private fun handlePerson(person: Person){
        if(!sql.isPersonExistInDatabase(person))
            sql.insertPersonInDatabase(person)
    }

}