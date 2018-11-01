package com.notesandroid.codex.notesandroid.retrofit

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.NotesAPI.Queries
import io.reactivex.Notification
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by Shiplayer on 23.10.18.
 */

/**
 * Class where implementing methods that receive response with json body from server.
 */

class CodeXNotesApi {

    /**
     *
     */
    val obsPersonContent = PublishSubject.create<Notification<Content>>()

    companion object {
        /**
         * Lazy method for instance through which we can invoke methods to send request and receive response.
         */
        private val api by lazy {
            Log.i("CodeXNotesApi", "service is created")
            CodeXNotesApiService.create()
        }
        /**
         * Lazy method for transform from json object to in the required object.
         */
        private val gson by lazy {
            GsonBuilder().create()
        }
    }

    /**
     * Transform [Queries] list to body for POST  query
     *
     * @param queries [Queries] list
     */
    fun buildQuery(vararg queries: String): String = """{ "query":"query { ${queries.joinToString("")} }" }"""

    /**
     * Sending request to CodeXNotes server and emits notification about response.
     *
     * @param userId - using user id after authorization on CodeXNote server.
     * @param jwt - using jwt after authorization on CodeXNote server.
     * @return Publish subject on [Content] wrapped in [Notification] for handling error on low level without
     * breaking the chain.
     * @see CodeXNotesApiService
     */

    @SuppressLint("CheckResult")
    fun getPersonContent(userId: String, jwt: String): PublishSubject<Notification<Content>> {
        val jsonElem = JsonParser().parse(buildQuery(Queries.getPersonContent(userId)))
        Log.i(CodeXNotesApi::class.java.simpleName, jsonElem.toString())
        api.getPersonContent(jsonElem.asJsonObject, "Bearer $jwt").subscribeOn(Schedulers.io()).subscribe({ next ->
            Log.i(CodeXNotesApi::class.java.simpleName, "get next: " + next.body()!!.toString())
            obsPersonContent.onNext(Notification.createOnNext(gson.fromJson(next.body()!!.asJsonObject["data"].asJsonObject["personContent"], Content::class.java)))
        }, { error ->
            error.printStackTrace()
            // Log.i(CodeXNotesApi::class.java.simpleName, error.)
            obsPersonContent.onNext(Notification.createOnError(error))
        }, {
            Log.i(CodeXNotesApi::class.java.simpleName, "GetPersonContent is completed")
            obsPersonContent.onComplete()
        })
        return obsPersonContent
    }

    /**
     * Authorization in CodeXNotes using Google token.
     *
     * @param token - received google token after authorization via google accounts.
     * @return Single observable with type [ServerAuthorizationResponse] for handling response in other thread.
     */

    fun authorization(token: String): Single<ServerAuthorizationResponse> {
        return api.userAuthorization(token).subscribeOn(Schedulers.io())
    }
}

/**
 * @param jwt - custom jwt from codex server
 * @param photo - profile photo url
 * @param dtModify - time of creation
 * @param channel - socket channel name
 * @param name - user full name
 */
data class ServerAuthorizationResponse(
    var jwt: String,
    var photo: String,
    var dtModify: String,
    var channel: String,
    var name: String
)