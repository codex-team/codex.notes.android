package com.notesandroid.codex.notesandroid

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.Database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.Essences.Folder
import com.notesandroid.codex.notesandroid.Essences.Person
import com.notesandroid.codex.notesandroid.Essences.User
import com.notesandroid.codex.notesandroid.NotesAPI.NotesAPI
import com.notesandroid.codex.notesandroid.NotesAPI.Queries
import com.notesandroid.codex.notesandroid.SharedPreferenceDatabase.UserData
import com.notesandroid.codex.notesandroid.Utilities.Utilities
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/*



 */
/**
 * Created by AksCorp on 04.02.2018.
 *
 * Load content from server by GRAPHQL_URL URL and save it to local database
 *
 * @property db current project database
 * @property context parent activity context
 */
class SaveDataFromServerTemplate(val db: LocalDatabaseAPI, val context: Context) {

    /**
     * Get person content by person ID
     *
     * @param personID content on a person's ID
     * @param callback function which wll be call after content load
     */
    fun loadContent(user: User, callback: (String) -> Unit) {

        val notesAPI = NotesAPI(user.jwt.toString()!!)
        notesAPI.executeQuery(GRAPHQL_URL,
            notesAPI.buildQuery(Queries.getPersonContent(user.info!!.id!!)),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (!Utilities.isInternetConnected(context))
                        callback(context.getString(R.string.offline_mod))
                    else
                        callback(context.getString(R.string.unexpected_error))
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        onFailure(
                            call,
                            IOException(response.code().toString() + response.message())
                        )
                        return
                    }
                    val resp = response.body()?.string()
                    val content: Content = parseResult(resp!!)

                    for (folder in content.folders.filter { it.owner == user.info }) {
                        addOrUpdateDB(folder)
                    }

                    for (folder in content.folders.filter { it.owner != user.info }) {
                        if (folder.owner != user.info) {
                            notesAPI.executeQuery(GRAPHQL_URL,
                                notesAPI.buildQuery(Queries.getPersonInfo(folder.owner!!.id!!)), object : Callback {
                                override fun onFailure(call: Call?, e: IOException?) {
                                    Throwable(e)
                                }

                                override fun onResponse(call: Call?, response: Response) {
                                    val gson = GsonBuilder().create()
                                    Log.i("SaveDataFromServer", response.body()?.string())
                                    val body = response.body()?.string()
                                    val writer = JsonParser()
                                    var jsonElement = writer.parse(body)
                                    val person = gson.fromJson(jsonElement.asJsonObject["data"], Person::class.java)
                                    if (db.isPersonExistInDatabase(person))
                                        db.updatePersonInDatabase(person)
                                    else
                                        db.insertPersonInDatabase(person)
                                    addOrUpdateDB(folder)
                                }
                            })
                        }
                    }

                    val date =
                        SimpleDateFormat(SYNC_TIME_FORMAT).format(Calendar.getInstance().time)
                    context.getSharedPreferences(UserData.NAME, 0).edit()
                        .putString(UserData.FIELDS.LAST_SYNC, date).apply()

                    callback(context.getString(R.string.sync_successful))
                }

                fun addOrUpdateDB(folder: Folder) {
                    if (db.isFolderExistInDatabase(folder))
                        db.updateFolderInDatabase(folder)
                    else
                        db.insertFolderInDatabase(folder)

                    for (note in folder.notes!!) {
                        note.folderId = folder.id

                        if (note.isRemoved!!)
                            continue

                        if (db.isNoteExistInDatabase(note))
                            db.updateNoteInDatabase(note)
                        else
                            db.insertNoteInDatabase(note)
                    }
                }

                fun parseResult(response: String): Content {
                    Log.i("SaveDataFromServer", response)
                    val builder = GsonBuilder()
                    val gson = builder.create()

                    try {
                        val writer = JsonParser()
                        var jsonElement = writer.parse(response)
                        jsonElement = jsonElement.asJsonObject["data"]

                        val content = gson.fromJson(
                            jsonElement.asJsonObject["personContent"],
                            Content::class.java
                        )

                        content.rootFolder = content.folders.filter {
                            it.isRoot!!
                        }.getOrNull(0)

                        return content
                    } catch (e: IOException) {
                    }
                    return Content()
                }
            })
    }
}