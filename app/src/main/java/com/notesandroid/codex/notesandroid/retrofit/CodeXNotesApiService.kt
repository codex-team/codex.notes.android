package com.notesandroid.codex.notesandroid.retrofit

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.notesandroid.codex.notesandroid.Essences.Person
import com.notesandroid.codex.notesandroid.url
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Shiplayer on 23.10.18.
 */
/**
 * An interface that declares the methods that are used in requests
 */
interface CodeXNotesApiService {

    companion object {
        /**
         * For set up setting
         */
        fun create(): CodeXNotesApiService {
            // val client = OkHttpClient.Builder()
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()

            return retrofit.create(CodeXNotesApiService::class.java)
        }
    }

    @POST("graphql")
    fun getPersonInfo(
      @Body body: JsonObject
    ): Observable<Person>

    /**
     * Sending request on [url]gtaphql with body that have information about request person and
     * set up jwt in header
     * @param body - JsonObject that have information about current person
     * @param jwt - JWT token
     *
     * @return Observable with Response for processing response as it received
     */

    @POST("graphql")
    fun getPersonContent(
      @Body body: JsonObject,
      @Header("Authorization") jwt: String
    ): Observable<Response<JsonElement>>

    /**
     * Sending request on [url]oauth/mobile with token for authorization in system CodeXNotes
     * @param token - token that getting from Google sign-in
     * @return Single observable for once processing response as it received
     */

    @GET("oauth/mobile")
    fun userAuthorization(
      @Query("token") token: String
    ): Single<ServerAuthorizationResponse>
}