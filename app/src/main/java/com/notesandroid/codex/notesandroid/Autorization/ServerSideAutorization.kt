package com.notesandroid.codex.notesandroid.Autorization

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.notesandroid.codex.notesandroid.Activities.MainActivity
import okhttp3.*
import okhttp3.HttpUrl
import java.io.IOException
import com.auth0.android.jwt.JWT
import com.notesandroid.codex.notesandroid.AUTHORIZATION_URL
import com.notesandroid.codex.notesandroid.ControlUserData
import com.notesandroid.codex.notesandroid.Database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.Utilities.MessageSnackbar
import com.notesandroid.codex.notesandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.runOnUiThread

/**
 * Created by AksCorp on 24.02.2018.
 *
 * This class interact with server and post/get jwt token for authorization
 *
 * @param context parent activity context
 * @param snackbarNotification snackbar for notification
 */
class ServerSideAutorization(val context: Context, private val snackbarNotification: MessageSnackbar) {
    
    /**
     * Create POST query for get custom JWT token from codex.notes.server
     *
     * @param googleToken string with jwt google token
     * @param callback [jwtTokenCallback]
     */
    private fun getCustomJWT(googleToken: String, callback: Callback) {
        
        val client = OkHttpClient()
        
        val urlBuilder = HttpUrl.parse(AUTHORIZATION_URL)!!.newBuilder()
        urlBuilder.addQueryParameter("token", googleToken)
        
        val request = Request.Builder()
                .url(urlBuilder.build())
                .addHeader("token", googleToken)
                .build()
        client.newCall(request).enqueue(callback)
    }
    
    /**
     * Get google JWT token and create callback
     *
     * @param completedTask google authorization task element
     */
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, db: LocalDatabaseAPI) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val googleToken = account.idToken
            
            getCustomJWT(googleToken!!, jwtTokenCallback(db))
        } catch (e: ApiException) {
            Log.e("Auth", ">>>>>>>>>>>>>>>>>>>>>>>>>>>> handleSignInResult", e);
            errorNotification()
        }
    }
    
    /**
     * Receive JWT token by codex.notes server and parse it
     */
    private fun jwtTokenCallback(db: LocalDatabaseAPI): Callback {
        return object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                
                try {
                    val token = response!!.body()?.string()
                    val jwt = JWT(token!!)
                    
                    ControlUserData(db, context).initUserInformation(jwt, token)
                } catch (e: Exception) {
                    Log.e("Auth", ">>>>>>>>>>>>>>>>>>>>>>>>>>>> JWT parsing error", e);
                    errorNotification()
                    return
                }
                
                context.runOnUiThread {
                    (context as MainActivity).sign_in_button.isEnabled = true
                    context.startInit()
                }
            }
            
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("Auth", ">>>>>>>>>>>>>>>>>>>>>>>>>>>> JWT request error", e);
                errorNotification()
                return
            }
        }
    }
    
    /**
     * Show error shackbar
     */
    fun errorNotification() {
        if (!Utilities.isInternetConnected(context))
            snackbarNotification.show(context.getString(R.string.no_internet_connection_available))
        else
            snackbarNotification.show(context.getString(R.string.autorization_failed))
        context.runOnUiThread {
            (context as MainActivity).sign_in_button.isEnabled = true
        }
    }
}
