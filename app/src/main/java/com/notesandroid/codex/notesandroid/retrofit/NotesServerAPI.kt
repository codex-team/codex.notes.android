package com.notesandroid.codex.notesandroid.retrofit

import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by AksCorp on 31.01.2018.
 *
 * This class work with Codex.notes.server GraphQL API
 *
 */

/**
 * Codex.notes.server API queries
 */
interface QueriesAPI {
    fun getPersonInfo(
      userId: String
    ): String
    fun getPersonContent(
      userId: String
    ): String
    fun getFolderContent(
      userId: String,
      folderId: String
    ): String
    fun getNoteContent(
      authorId: String,
      folderId: String,
      noteId: String
    ): String
}

/**
 * Codex.notes.server API mutations
 */
interface MutationsAPI {
    fun createFolder(
      name: String
    )
}

/**
 * Queries describe
 */
class Queries {
    companion object : QueriesAPI {

        /**
         *
         */
        override fun getPersonInfo(
          userId: String
        ): String = """
          Person: user(id: \"$userId\") {
            id
            name
            email
          }
    """.trimIndent().replace("\n", "")

        override fun getPersonContent(
          userId: String
        ): String = """
          personContent: user(id: \"$userId\") {
            folders {
              id
              title
              isRoot
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
                  photo
                }
                isRemoved
              }
            }
          }
    """.trimIndent().replace("\n", "")

        override fun getFolderContent(
          ownerId: String,
          folderId: String
        ): String = """
          personContent: user(id: \"$folderId\", ownerId: \"$ownerId\") {
              id
              title
              isRoot
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
                  photo
                }
                isRemoved
              }
          }
    """.trimIndent().replace("\n", "")

        override fun getNoteContent(
          authorId: String,
          folderId: String,
          noteId: String
        ): String = """
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
                  photo
                }
                isRemoved
          }
    """.trimIndent().replace("\n", "")
    }
}

/**
 * Mutations describe
 */
class Mutations {
    companion object : MutationsAPI {
        override fun createFolder(
          name: String
        ) {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }
    }
}

/**
 * Control graphql queries
 */
class NotesAPI(
  private val jwt: String
) {
    /**
     * Transform [Queries] list to body for POST  query
     *
     * @param queries [Queries] list
     */
    fun buildQuery(
      vararg queries: String
    ): String = """{ "query":"query { ${queries.joinToString("")} }" }"""

    /**
     * @param url post url
     * @param query string for post query Create in [buildQuery]
     * @param callback
     */
    fun executeQuery(
      url: String,
      query: String,
      callback: Callback
    ) {
        val client = OkHttpClient()
        val contentType = MediaType.parse("application/json; charset=utf-8")

        val body = RequestBody.create(contentType, query)
        val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + jwt)
                .post(body)
                .build()
        client.newCall(request).enqueue(callback)
    }
}