package com.notesandroid.codex.notesandroid.Authorization

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.GsonBuilder
import com.notesandroid.codex.notesandroid.AUTHORIZATION_URL
import com.notesandroid.codex.notesandroid.Activities.MainActivity
import com.notesandroid.codex.notesandroid.ControlUserData
import com.notesandroid.codex.notesandroid.Database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.Utilities.MessageSnackbar
import com.notesandroid.codex.notesandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.nav_view_menu.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.runOnUiThread
import java.io.IOException

/**
 * Created by AksCorp on 24.02.2018.
 *
 * This class interact with server and post/get jwt token for authorization
 *
 * @param context parent activity context
 * @param snackbarNotification snackbar for notification
 */
class ServerSideAutorization(val context: Context,
    private val snackbarNotification: MessageSnackbar)
{
    
    /**
     * Create POST query for get custom JWT token from codex.notes.server
     *
     * @param googleToken string with jwt google token
     * @param callback [jwtTokenCallback]
     */
    private fun getCustomJWT(googleToken: String, callback: Callback)
    {
        
        val client = OkHttpClient()
        
        val urlBuilder = HttpUrl.parse(AUTHORIZATION_URL)!!.newBuilder()
        urlBuilder.addQueryParameter("token", googleToken)
        
        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()
        client.newCall(request).enqueue(callback)
    }
    
    /**
     * Get google JWT token and create callback
     *
     * @param completedTask google authorization task element
     */
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, db: LocalDatabaseAPI)
    {
        try
        {
            val account = completedTask.getResult(ApiException::class.java)
            val googleToken = account.idToken
    
            getCustomJWT(googleToken!!, jwtTokenCallback(db))
        } catch (e: ApiException)
        {
            errorNotification()
        }
    }
    
    /**
     * Receive JWT token by codex.notes server and parse it
     */
    private fun jwtTokenCallback(db: LocalDatabaseAPI): Callback
    {
        return object : Callback
        {
            override fun onResponse(call: Call?, response: Response?)
            {
                
                try
                {
                    val builder = GsonBuilder()
                    val gson = builder.create()
                    val response = response!!.body()?.string()
                    val responseJson = gson.fromJson(response,
                        ServerAuthorizationResponse::class.java)
                    
                    ControlUserData(db, context).initUserInformation(responseJson)
                } catch (e: Exception)
                {
                    errorNotification()
                    return
                }
                
                context.runOnUiThread {
                    (context as MainActivity).startInit()
                }
            }
            
            override fun onFailure(call: Call?, e: IOException?)
            {
                errorNotification()
                return
            }
        }
    }
    
    /**
     * Show error shackbar
     */
    fun errorNotification()
    {
        if (!Utilities.isInternetConnected(context))
            snackbarNotification.show(context.getString(R.string.no_internet_connection_available))
        else
            snackbarNotification.show(context.getString(R.string.autorization_failed))
        context.runOnUiThread {
            (context as MainActivity).header_layout.isClickable = true
            
        }
    }
}

/**
 * @param jwt - custom jwt from codex server
 * @param photo - profile photo url
 * @param dtModify - time of creation
 * @param channel - socket channel name
 * @param name - user full name
 */
data class ServerAuthorizationResponse(var jwt: String, var photo: String, var dtModify:
String, var channel: String, var name: String)
