package com.notesandroid.codex.notesandroid.retrofit

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.Authorization.ServerAuthorizationResponse
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.NotesAPI.Queries
import io.reactivex.Notification
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by Shiplayer on 23.10.18.
 */

class CodeXNotesApi{

    val obsPersonContent = PublishSubject.create<Notification<Content>>()

    companion object {
        private val api by lazy{
            Log.i("CodeXNotesApi", "service is created")
            CodeXNotesApiService.create()
        }
        private val gson by lazy{
            GsonBuilder().create()
        }
    }

    /**
     * Transform [Queries] list to body for POST  query
     *
     * @param queries [Queries] list
     */
    fun buildQuery(vararg queries: String): String = """{ "query":"query { ${queries.joinToString("")} }" }"""

    fun getPersonContent(userId:String, jwt:String):PublishSubject<Notification<Content>> {
        val jsonElem = JsonParser().parse(buildQuery(Queries.getPersonContent(userId)))
        Log.i(CodeXNotesApi::class.java.simpleName, jsonElem.toString())
        api.getPersonContent(jsonElem.asJsonObject, "Bearer $jwt").subscribeOn(Schedulers.io()).subscribe({ next ->
            Log.i(CodeXNotesApi::class.java.simpleName, "get next: " + next.body()!!.toString())
            obsPersonContent.onNext(Notification.createOnNext(gson.fromJson(next.body()!!.asJsonObject["data"].asJsonObject["personContent"], Content::class.java)))
        }, { error ->
            error.printStackTrace()
            //Log.i(CodeXNotesApi::class.java.simpleName, error.)
            obsPersonContent.onNext(Notification.createOnError(error))
        }, {
            Log.i(CodeXNotesApi::class.java.simpleName, "GetPersonContent is completed")
            obsPersonContent.onComplete()
        })
        return obsPersonContent
    }

    fun authorization(token:String): Single<ServerAuthorizationResponse> {
        return api.userAuthorization(token).subscribeOn(Schedulers.io())

    }

}