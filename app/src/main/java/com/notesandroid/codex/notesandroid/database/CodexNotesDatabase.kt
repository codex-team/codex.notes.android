package com.notesandroid.codex.notesandroid.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.notesandroid.codex.notesandroid.database.tables.Folders
import com.notesandroid.codex.notesandroid.database.tables.Notes
import com.notesandroid.codex.notesandroid.database.tables.Persons
import org.jetbrains.anko.db.AUTOINCREMENT
import org.jetbrains.anko.db.INTEGER
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.PRIMARY_KEY
import org.jetbrains.anko.db.TEXT
import org.jetbrains.anko.db.createTable
import org.jetbrains.anko.db.dropTable

/**
 * Created by AksCorp on 01.02.2018.
 *
 * Start database initializations
 */

const val DATABASE_NAME = "CodexNotesDatabase"
const val DATABASE_VERSION = 1

/**
 * @param context parent activity context
 */
class CodexNotesDatabase(
  context: Context
) : ManagedSQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private var instance: CodexNotesDatabase? = null

        /**
         * Get current database exemplar
         */
        @Synchronized
        fun getInstance(
          context: Context
        ): CodexNotesDatabase {
            if (instance == null) {
                instance = CodexNotesDatabase(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(
      db: SQLiteDatabase
    ) {
        db.createTable(Notes.NAME, true,
                Notes.FIELDS._ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Notes.FIELDS.ID to TEXT,
                Notes.FIELDS.FOLDER_ID to TEXT,
                Notes.FIELDS.TITLE to TEXT,
                Notes.FIELDS.CONTENT to TEXT,
                Notes.FIELDS.DT_CREATE to TEXT,
                Notes.FIELDS.DT_MODIFY to TEXT,
                Notes.FIELDS.AUTHOR_ID to TEXT,
                Notes.FIELDS.IS_REMOVED to TEXT
        )
        db.createTable(Persons.NAME, true,
                Persons.FIELDS.PERSON_ID to TEXT,
                Persons.FIELDS.NAME to TEXT,
                Persons.FIELDS.EMAIL to TEXT
        )

        db.createTable(Folders.NAME, true,
                Folders.FIELDS._ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Folders.FIELDS.ID to TEXT,
                Folders.FIELDS.TITLE to TEXT,
                Folders.FIELDS.OWNER_ID to TEXT,
                Folders.FIELDS.IS_ROOT to TEXT
        )
    }

    override fun onUpgrade(
      db: SQLiteDatabase,
      oldVersion: Int,
      newVersion: Int
    ) {
        db.dropTable(Notes.NAME, true)
        db.dropTable(Persons.NAME, true)
        db.dropTable(Folders.NAME, true)

        onCreate(db)
    }
}