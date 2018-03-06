package com.notesandroid.codex.notesandroid.Database.Tables

/**
 * Created by AksCorp on 01.02.2018.
 */

object Persons {

    const val NAME = "Persons"


    object FIELDS {
        val PERSON_ID = "personId"
        val NAME = "name"
        val EMAIL = "email"
    }
}

object Notes {

    const val NAME = "Notes"

    object FIELDS {
        val _ID = "_id"
        val ID = "noteId"
        val FOLDER_ID = "folderId"
        val TITLE = "title"
        val CONTENT = "content"
        val DT_CREATE = "dtCreate"
        val DT_MODIFY = "dtModify"
        val AUTHOR_ID = "authorId"
        val IS_REMOVED = "idRemoved"
    }
}

object Folders {

    const val NAME = "Folders"

    object FIELDS {
        val _ID = "_id"
        val ID = "folderId"
        val TITLE = "title"
        val OWNER_ID = "ownerId"
    }
}