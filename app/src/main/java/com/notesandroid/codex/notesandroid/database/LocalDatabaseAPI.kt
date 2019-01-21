package com.notesandroid.codex.notesandroid.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import com.notesandroid.codex.notesandroid.database.tables.Folders
import com.notesandroid.codex.notesandroid.database.tables.Notes
import com.notesandroid.codex.notesandroid.database.tables.Persons
import com.notesandroid.codex.notesandroid.data.Folder
import com.notesandroid.codex.notesandroid.data.Note
import com.notesandroid.codex.notesandroid.data.Person
import org.jetbrains.anko.db.insertOrThrow

/**
 * Created by AksCorp on 01.02.2018.
 *
 * Local database API
 *
 * @param context parent context
 */
public class LocalDatabaseAPI(
  private val context: Context
) {

    /**
     * Insert essence if it's not exist in database
     */
    fun insertIfNotExist(
      el: Any
    ) {
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

    /**
     * Get person essence from database by id
     *
     * @param personId person id
     * @return person essence
     */
    fun getPersonFromDatabase(
      personId: String
    ): Person {
        return CodexNotesDatabase.getInstance(context).use {
            val cursor: Cursor
            try {
                cursor = query(Persons.NAME, null, "${Persons.FIELDS.PERSON_ID} = \"$personId\"", null, null, null, null)
            } catch (
              e: SQLiteException
            ) {
                throw Exception("Table ${Persons.NAME} doesn't exist")
            }
            cursor.moveToFirst()
            if (cursor.count > 1)
                throw Exception("Wrong database. ${cursor.count} person with ID = $personId")
            if (cursor.count == 0)
                throw Exception("Person with ID = $personId doesn't exist")

            val person = Person(null, null, null, null)

            var pos = 0
            for (columnName in cursor.columnNames) {
                when (columnName) {
                    Persons.FIELDS.PERSON_ID -> person.id = cursor.getString(pos)
                    Persons.FIELDS.NAME -> person.name = cursor.getString(pos)
                    Persons.FIELDS.EMAIL -> person.email = cursor.getString(pos)
                }
                pos++
            }
            return@use person
        }
    }

    /**
     * Get all notes essence from database by folder id
     *
     * @param folderId folder id
     * @return list notes essence
     */
    fun getNotesFromDatabase(
      folderId: String
    ): MutableList<Note> {
        return CodexNotesDatabase.getInstance(context).use {
            var cursor: Cursor
            try {

                cursor = query(Notes.NAME, null, "${Notes.FIELDS.FOLDER_ID} = \"$folderId\"", null, null, null, null)
            } catch (
              e: SQLiteException
            ) {
                return@use mutableListOf<Note>()
            }
            cursor.moveToFirst()

            val notes = mutableListOf<Note>()

            if (cursor.count == 0)
                return@use notes

            do {

                val note = Note()
                for ((pos, columnName) in cursor.columnNames.withIndex()) {
                    when (columnName) {
                        Notes.FIELDS.ID -> note.id = cursor.getString(pos)
                        Notes.FIELDS.AUTHOR_ID -> note.author = getPersonFromDatabase(cursor.getString(pos))
                        Notes.FIELDS.CONTENT -> note.content = cursor.getString(pos)
                        Notes.FIELDS.DT_CREATE -> note.dtCreate = cursor.getString(pos)
                        Notes.FIELDS.DT_MODIFY -> note.dtModify = cursor.getString(pos)
                        Notes.FIELDS.FOLDER_ID -> note.folderId = cursor.getString(pos)
                        Notes.FIELDS.IS_REMOVED -> note.isRemoved = cursor.getString(pos).toBoolean()
                        Notes.FIELDS.TITLE -> note.title = cursor.getString(pos)
                    }
                }
                notes.add(note)
            } while (cursor.moveToNext())

            return@use notes
        }
    }

    /**
     * Get all folders essence from database
     *
     * @return list folders essence
     */
    fun getFoldersFromDatabase(): MutableList<Folder> {
        return CodexNotesDatabase.getInstance(context).use {
            var cursor: Cursor
            try {

                cursor = query(Folders.NAME, null, null, null, null, null, null)
            } catch (
              e: SQLiteException
            ) {
                return@use mutableListOf<Folder>()
            }
            cursor.moveToFirst()

            val folders = mutableListOf<Folder>()

            if (cursor.count == 0)
                return@use folders

            do {

                val folder = Folder()
                for ((pos, columnName) in cursor.columnNames.withIndex()) {
                    when (columnName) {
                        Folders.FIELDS.ID -> folder.id = cursor.getString(pos)
                        Folders.FIELDS.TITLE -> folder.title = cursor.getString(pos)
                        Folders.FIELDS.OWNER_ID -> folder.owner = getPersonFromDatabase(cursor.getString(pos))
                        Folders.FIELDS.IS_ROOT -> folder.isRoot = cursor.getString(pos) == "1"
                    }
                }
                folders.add(folder)
            } while (cursor.moveToNext())

            return@use folders
        }
    }

    /**
     * Insert note_list_element in database
     *
     * @param note note_list_element to add to the database
     */
    fun insertNoteInDatabase(
      note: Note
    ) {
        CodexNotesDatabase.getInstance(context).use {
            if (note.author != null && !isPersonExistInDatabase(note.author!!))
                insertPersonInDatabase(note.author!!)
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

    /**
     * Update note_list_element in database by note_list_element id
     *
     * @param note note_list_element to update to the database
     */
    fun updateNoteInDatabase(
      note: Note
    ) {
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

    /**
     * @param note note_list_element to check the existence to the database
     * @return true if exist, or false
     */
    fun isNoteExistInDatabase(
      note: Note
    ): Boolean {
        return CodexNotesDatabase.getInstance(context).use {
            val cursor: Cursor
            try {
                cursor = query(Notes.NAME, arrayOf(Notes.FIELDS.ID), "${Notes.FIELDS.ID} = \"${note.id}\"", null, null, null, null)
            } catch (
              e: SQLiteException
            ) {
                throw Exception("Table ${Notes.NAME} doesn't exist")
            }
            return@use cursor.count > 0
        }
    }

    /**
     * Insert person in database
     *
     * @param person person to add to the database
     */
    fun insertPersonInDatabase(
      person: Person
    ) {
        CodexNotesDatabase.getInstance(context).use {
            insertOrThrow(Persons.NAME,
                    Persons.FIELDS.PERSON_ID to person.id,
                    Persons.FIELDS.NAME to person.name,
                    Persons.FIELDS.EMAIL to person.email
            )
        }
    }

    /**
     * Update person in database by person id
     *
     * @param person person to update to the database
     */
    fun updatePersonInDatabase(
      person: Person
    ) {
        CodexNotesDatabase.getInstance(context).use {
            val personValues = ContentValues()
            personValues.put(Persons.FIELDS.PERSON_ID, person.id)
            personValues.put(Persons.FIELDS.NAME, person.name)
            personValues.put(Persons.FIELDS.EMAIL, person.email)
            update(Persons.NAME, personValues, "${Persons.FIELDS.PERSON_ID} = \"${person.id}\"", null)
        }
    }

    /**
     * @param person person to check the existence to the database
     * @return true if exist, or false
     */
    fun isPersonExistInDatabase(
      person: Person
    ): Boolean {
        return CodexNotesDatabase.getInstance(context).use {
            val cursor: Cursor
            try {

                cursor = query(Persons.NAME, arrayOf(Persons.FIELDS.PERSON_ID), "${Persons.FIELDS.PERSON_ID} = \"${person.id}\"", null, null, null, null)
            } catch (
              e: SQLiteException
            ) {
                throw Exception("Table ${Persons.NAME} doesn't exist")
            }
            return@use cursor.count > 0
        }
    }

    /**
     * Insert folder in database
     *
     * @param folder folder to add to the database
     */
    fun insertFolderInDatabase(
      folder: Folder
    ) {
        CodexNotesDatabase.getInstance(context).use {
            insertOrThrow(Folders.NAME,
                    Folders.FIELDS.ID to folder.id,
                    Folders.FIELDS.TITLE to folder.title,
                    Folders.FIELDS.OWNER_ID to folder.owner?.id,
                    Folders.FIELDS.IS_ROOT to folder.isRoot
            )
        }
    }

    /**
     * Update folder in database by person id
     *
     * @param folder folder to update to the database
     */
    fun updateFolderInDatabase(
      folder: Folder
    ) {
        CodexNotesDatabase.getInstance(context).use {
            val folderValue = ContentValues()
            folderValue.put(Folders.FIELDS.ID, folder.id)
            folderValue.put(Folders.FIELDS.TITLE, folder.title)
            folderValue.put(Folders.FIELDS.OWNER_ID, folder.owner?.id)
            update(Folders.NAME, folderValue, "${Folders.FIELDS.ID} = \"${folder.id}\"", null)
        }
    }

    /**
     * @param folder folder to check the existence to the database
     * @return true if exist, or false
     */
    fun isFolderExistInDatabase(
      folder: Folder
    ): Boolean {
        return CodexNotesDatabase.getInstance(context).use {
            val cursor: Cursor
            try {
                cursor = query(Folders.NAME, arrayOf(Folders.FIELDS.ID), "${Folders.FIELDS.ID} = \"${folder.id}\"", null, null, null, null)
            } catch (
              e: SQLiteException
            ) {
                throw Exception("Table ${Folders.NAME} doesn't exist")
            }
            return@use cursor.count > 0
        }
    }

    /**
     * Remove all data from local database
     */
    fun deleteDatabase() {
        context.deleteDatabase(DATABASE_NAME)
    }
}