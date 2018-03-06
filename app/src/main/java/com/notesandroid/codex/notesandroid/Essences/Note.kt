package com.notesandroid.codex.notesandroid.Essences

/**
 * Created by AksCorp on 31.01.2018.
 */
class Note(val id: String, var folderId: String, val title: String, val content: String, val dtCreate: String, val dtModify: String,
           val author: Person?, val isRemoved: String) {
}