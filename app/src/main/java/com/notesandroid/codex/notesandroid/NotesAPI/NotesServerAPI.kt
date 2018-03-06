package com.notesandroid.codex.notesandroid.NotesAPI

import android.content.Context
import okhttp3.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.ApplicationState.Companion.currentUser
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.SharedPreferenceDatabase.UserData
import java.io.*


/**
 * This class work with Codex.notes.server GraphQL API
 *
 * Created by AksCorp on 31.01.2018.
 */


const val GRAPHQL_URL = "https://api.notes.ifmo.su/graphql"

/**
 * Codex.notes.server API queries
 */
interface QueriesAPI {
    fun getPersonInfo(userId: String): String
    fun getPersonContent(userId: String): String
    fun getFolderContent(userId: String, folderId: String): String
    fun getNoteContent(authorId: String, folderId: String, noteId: String): String
}

class Queries {
    companion object : QueriesAPI {


        /**
         *
         */
        override fun getPersonInfo(userId: String): String = """
          Person: user(id: \"$userId\") {
            id
            name
            email
          }
    """.trimIndent().replace("\n", "")


        override fun getPersonContent(userId: String): String = """
          personContent: user(id: \"$userId\") {
            folders {
              id
              title
              owner {
                name
                id
              }
              notes {
                id
                title
                content
                dtCreate
                dtModify
                author {
                  id
                  name
                  email
                }
                isRemoved
              }
            }
          }
    """.trimIndent().replace("\n", "")

        override fun getFolderContent(ownerId: String, folderId: String): String = """
          personContent: user(id: \"$folderId\", ownerId: \"$ownerId\") {
              id
              title
              owner {
                name
                id
              }
              notes {
                id
                title
                content
                dtCreate
                dtModify
                author {
                  id
                  name
                  email
                }
                isRemoved
              }
          }
    """.trimIndent().replace("\n", "")


        override fun getNoteContent(authorId: String, folderId: String, noteId: String): String = """
          personContent: user(id: \"$noteId\", authorId: \"$authorId\", folderId: \"$folderId\") {
                id
                title
                content
                dtCreate
                dtModify
                author {
                  id
                  name
                  email
                }
                isRemoved
          }
    """.trimIndent().replace("\n", "")


        fun buildQuery(vararg queries: String): String = """{ "query":"query { ${queries.joinToString("")} }" }"""

        fun executeQuery(url: String, query: String, callback: Callback) {
            val client = OkHttpClient()
            val contentType = MediaType.parse("application/json; charset=utf-8");

            val body = RequestBody.create(contentType, query)
            val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer "+currentUser.jwt.toString())
                    .post(body)
                    .build()
            client.newCall(request).enqueue(callback);
        }
    }
}