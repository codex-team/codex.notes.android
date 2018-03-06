package com.notesandroid.codex.notesandroid

import android.content.Context
import android.provider.Settings.Global.getString
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.Database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.Essences.Person
import com.notesandroid.codex.notesandroid.Essences.User
import com.notesandroid.codex.notesandroid.NotesAPI.Queries
import com.notesandroid.codex.notesandroid.Utilities.MessageDialog
import com.notesandroid.codex.notesandroid.Utilities.MessageSnackbar
import com.notesandroid.codex.notesandroid.Utilities.Utilities
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import android.app.Activity
import android.support.design.widget.CoordinatorLayout
import android.view.ViewGroup


/**
 * Created by AksCorp on 04.02.2018.
 */

class LoadDataFromServer(val db: LocalDatabaseAPI, val context: Context) {
    
    fun loadContent(personID: String, callback: (String) -> Unit) {
        
        Queries.executeQuery(GRAPHQL_URL, Queries.buildQuery(Queries.getPersonContent(personID)),
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
                            onFailure(call, IOException(response.code().toString() + response.message()))
                            return
                        }
                        val resp = response.body()?.string()
                        val content: Content = parseResult(resp!!)
                        
                        for (folder in content.folders) {
                            if (db.isFolderExistInDatabase(folder))
                                db.updateFolderInDatabase(folder)
                            else
                                db.insertFolderInDatabase(folder)
                            
                            
                            for (note in folder.notes) {
                                note.folderId = folder.id
                                if (db.isNoteExistInDatabase(note))
                                    db.updateNoteInDatabase(note)
                                else
                                    db.insertNoteInDatabase(note)
                            }
                        }
                        callback(context.getString(R.string.sync_successful))
                    }
                    
                    fun parseResult(response: String): Content {
                        val builder = GsonBuilder()
                        val gson = builder.create()
                        
                        try {
                            val writer = JsonParser()
                            var jsonElement = writer.parse(response)
                            jsonElement = jsonElement.asJsonObject["data"]
                            
                            val content = gson.fromJson(jsonElement.asJsonObject["personContent"], Content::class.java)
                            
                            return content
                        } catch (e: IOException) {
                        
                        }
                        return Content(listOf())
                    }
                })
    }
    
    fun loadPersonData(personID: String, callback: (String) -> Unit) {
        Queries.executeQuery(GRAPHQL_URL, Queries.buildQuery(Queries.getPersonInfo(personID)),
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
                            onFailure(call, IOException(response.code().toString() + response.message()))
                            return
                        }
                        val resp = response.body()?.string()
                        val person = parseResult(resp!!)
                        
                        if (db.isPersonExistInDatabase(person!!))
                            db.updatePersonInDatabase(person!!)
                        else
                            db.insertPersonInDatabase(person)
                        
                    }
                    
                    fun parseResult(response: String): Person? {
                        val builder = GsonBuilder()
                        val gson = builder.create()
                        val writer = JsonParser()
                        var jsonElement = writer.parse(response)
                        jsonElement = jsonElement.asJsonObject["data"]
                        
                        val person = gson.fromJson(jsonElement.asJsonObject["Person"], Person::class.java)
                        
                        return person
                        
                    }
                    
                })
    }
}