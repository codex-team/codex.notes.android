package com.notesandroid.codex.notesandroid.retrofit

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.Authorization.ServerAuthorizationResponse
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.NotesAPI.Queries
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
 * Created by Shiplayer on 23.10.18.
 */

class CodeXNotesApi{

    companion object {
        private val api by lazy{
            Log.i("CodeXNotesApi", "service is created")
            CodeXNotesApiService.create()
        }
        private val gson by lazy{
            GsonBuilder().create()
        }
    }

    fun getPersonContent(userId:String, jwt:String):Observable<Content>{
        val jsonElem = JsonParser().parse(Queries.getPersonInfo(userId))
        return api.getPersonContent(jsonElem.asJsonObject, jwt)
    }

    fun authorization(token:String):Observable<ServerAuthorizationResponse>{
        return Observable.create<ServerAuthorizationResponse> {
            api.userAuthorization(token)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { next ->
                        it.onNext(next.body()!!)
                        it.onComplete()
                    },
                    { error ->
                        val e = error as HttpException
                        //Log.i("CodeXNotesApi", e.response().raw())
                        error.printStackTrace()
                        it.onError(e)
                    })
        }

    }

}