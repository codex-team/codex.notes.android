package com.notesandroid.codex.notesandroid.Database

import android.content.ContentValues
import android.content.Context
import com.notesandroid.codex.notesandroid.Database.Tables.Folders
import com.notesandroid.codex.notesandroid.Database.Tables.Notes
import com.notesandroid.codex.notesandroid.Database.Tables.Persons
import com.notesandroid.codex.notesandroid.Essences.Folder
import com.notesandroid.codex.notesandroid.Essences.Note
import com.notesandroid.codex.notesandroid.Essences.Person
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.insertOrThrow

/**
 * Created by AksCorp on 01.02.2018.
 */
public class LocalDatabaseAPI(private val context: Context) {

    fun insertIfNotExist(el: Any) {
        when (el) {
            is Person ->
                if (!isPersonExistInDatabase(el))
                    insertPersonInDatabase(el)
            is Note ->
                if (!isNoteExistInDatabase(el))
                    insertNoteInDatabase(el)
            is Folder ->
                if (!isFolderExistInDatabase(el))
                    insertFolderInDatabase(el)
        }

    }

    fun getPersonFromDatabase(personId: String): Person {
        return CodexNotesDatabase.getInstance(context).use {
            val cursor = query(Persons.NAME, null, "${Persons.FIELDS.PERSON_ID} = \"$personId\"", null, null, null, null)
            cursor.moveToFirst()
            if (cursor.count > 1)
                throw Exception("Wrong database. ${cursor.count} person with ID = $personId")
            if (cursor.count == 0)
                throw Exception("Person with ID = $personId doesn't exist")

            val person = Person(null, null, null)

            var pos = 0;
            for (columnName in cursor.columnNames) {
                when (columnName) {
                    Persons.FIELDS.PERSON_ID -> person.id = cursor.getString(pos)
                    Persons.FIELDS.NAME -> person.name = cursor.getString(pos)
                    Persons.FIELDS.EMAIL -> person.email = cursor.getString(pos)
                }
                pos++;
            }
            return@use person
        }
    }

    fun insertNoteInDatabase(note: Note) {
        CodexNotesDatabase.getInstance(context).use {
            insertOrThrow(Notes.NAME,
                    Notes.FIELDS.ID to note.id,
                    Notes.FIELDS.FOLDER_ID to note.folderId,
                    Notes.FIELDS.TITLE to note.title,
                    Notes.FIELDS.CONTENT to note.content,
                    Notes.FIELDS.DT_CREATE to note.dtCreate,
                    Notes.FIELDS.DT_MODIFY to note.dtModify,
                    Notes.FIELDS.AUTHOR_ID to note.author?.id,
                    Notes.FIELDS.IS_REMOVED to note.isRemoved)
        }

    }

    fun updateNoteInDatabase(note: Note) {
        CodexNotesDatabase.getInstance(context).use {
            val noteValues = ContentValues()
            noteValues.put(Notes.FIELDS.FOLDER_ID, note.folderId)
            noteValues.put(Notes.FIELDS.TITLE, note.title)
            noteValues.put(Notes.FIELDS.CONTENT, note.content)
            noteValues.put(Notes.FIELDS.DT_CREATE, note.dtCreate)
            noteValues.put(Notes.FIELDS.DT_MODIFY, note.dtModify)
            noteValues.put(Notes.FIELDS.AUTHOR_ID, note.author?.id)
            noteValues.put(Notes.FIELDS.IS_REMOVED, note.isRemoved)
            update(Notes.NAME, noteValues, "${Notes.FIELDS.ID} = \"${note.id}\"", null)
        }
    }

    fun isNoteExistInDatabase(note: Note): Boolean {
        return CodexNotesDatabase.getInstance(context).use {
            val cursor = query(Notes.NAME, arrayOf(Notes.FIELDS.ID), "${Notes.FIELDS.ID} = \"${note.id}\"", null, null, null, null)
            return@use cursor.count > 0
        }
    }

    fun insertPersonInDatabase(person: Person) {
        CodexNotesDatabase.getInstance(context).use {
            insertOrThrow(Persons.NAME,
                    Persons.FIELDS.PERSON_ID to person.id,
                    Persons.FIELDS.NAME to person.name,
                    Persons.FIELDS.EMAIL to person.email
            )
        }
    }

    fun updatePersonInDatabase(person: Person) {
        CodexNotesDatabase.getInstance(context).use {
            val personValues = ContentValues()
            personValues.put(Persons.FIELDS.PERSON_ID, person.id)
            personValues.put(Persons.FIELDS.NAME, person.name)
            personValues.put(Persons.FIELDS.EMAIL, person.email)
            update(Persons.NAME, personValues, "${Persons.FIELDS.PERSON_ID} = \"${person.id}\"", null)
        }
    }

    fun isPersonExistInDatabase(person: Person): Boolean {
        return CodexNotesDatabase.getInstance(context).use {
            val cursor = query(Persons.NAME, arrayOf(Persons.FIELDS.PERSON_ID), "${Persons.FIELDS.PERSON_ID} = \"${person.id}\"", null, null, null, null)
            return@use cursor.count > 0
        }
    }

    fun insertFolderInDatabase(folder: Folder) {
        CodexNotesDatabase.getInstance(context).use {
            insertOrThrow(Folders.NAME,
                    Folders.FIELDS.ID to folder.id,
                    Folders.FIELDS.TITLE to folder.title,
                    Folders.FIELDS.OWNER_ID to folder.owner?.id
            )
        }
    }

    fun updateFolderInDatabase(folder: Folder) {
        CodexNotesDatabase.getInstance(context).use {
            val folderValue = ContentValues()
            folderValue.put(Folders.FIELDS.ID, folder.id)
            folderValue.put(Folders.FIELDS.TITLE, folder.title)
            folderValue.put(Folders.FIELDS.OWNER_ID, folder.owner?.id)
            update(Folders.NAME, folderValue, "${Folders.FIELDS.ID} = \"${folder.id}\"", null)
        }
    }

    fun isFolderExistInDatabase(folder: Folder): Boolean {
        return CodexNotesDatabase.getInstance(context).use {
            val cursor = query(Folders.NAME, arrayOf(Folders.FIELDS.ID), "${Folders.FIELDS.ID} = \"${folder.id}\"", null, null, null, null)
            return@use cursor.count > 0
        }
    }
}