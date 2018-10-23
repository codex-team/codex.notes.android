package com.notesandroid.codex.notesandroid.retrofit

import com.google.gson.JsonObject
import com.notesandroid.codex.notesandroid.Authorization.ServerAuthorizationResponse
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.Essences.Person
import com.notesandroid.codex.notesandroid.url
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Created by Shiplayer on 23.10.18.
 */

interface CodeXNotesApiService{

    companion object {
        fun create():CodeXNotesApiService {
            //val client = OkHttpClient.Builder()
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()

            return retrofit.create(CodeXNotesApiService::class.java)
        }
    }

    @GET("graphql")
    fun getPersonInfo(@Body body: JsonObject):Observable<Person>

    @GET("graphql")
    fun getPersonContent(@Body body: JsonObject, @Header("Authorization") jwt:String):Observable<Content>

    @GET("oauth/mobile")
    fun userAuthorization(@Query("token") token: String):Observable<Response<ServerAuthorizationResponse>>
}