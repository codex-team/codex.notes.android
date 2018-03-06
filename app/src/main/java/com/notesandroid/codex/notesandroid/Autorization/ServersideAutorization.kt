package com.notesandroid.codex.notesandroid.Autorization

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.notesandroid.codex.notesandroid.Activities.MainActivity
import com.notesandroid.codex.notesandroid.Database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.Essences.Person
import com.notesandroid.codex.notesandroid.SharedPreferenceDatabase.UserData
import okhttp3.*
import okhttp3.HttpUrl
import java.io.IOException
import com.auth0.android.jwt.JWT
import com.notesandroid.codex.notesandroid.Activities.SignInActivity
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.Utilities.MessageDialog
import com.notesandroid.codex.notesandroid.Utilities.MessageSnackbar
import com.notesandroid.codex.notesandroid.Utilities.MyLog
import com.notesandroid.codex.notesandroid.Utilities.Utilities
import kotlinx.android.synthetic.main.sign_in.*
import org.jetbrains.anko.runOnUiThread


/**
 * Created by AksCorp on 24.02.2018.
 */

const val url: String = "https://api.notes.ifmo.su/oauth/mobile"

class ServersideAutorization(val context: Context,val snackbarNotification: MessageSnackbar) {

    val log = MyLog(context)

    fun getCustomJWT(googleToken: String, callback: Callback) {

        val client = OkHttpClient()

        val urlBuilder = HttpUrl.parse(url)!!.newBuilder()
        urlBuilder.addQueryParameter("token", googleToken);

        val request = Request.Builder()
                .url(urlBuilder.build())
                .addHeader("token", googleToken)
                .build()
        client.newCall(request).enqueue(callback);
    }


    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val googleToken = account.idToken

            getCustomJWT(googleToken!!, object : Callback {
                override fun onResponse(call: Call?, response: Response?) {

                    try {
                        val token = response!!.body()?.string()
                        val jwt = JWT(token!!)
                        initUserData(jwt, token)
                    }
                    catch (e:Exception)
                    {
                        errorNotification()
                        return
                    }
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }


                private fun initUserData(jwt: JWT, token: String) {

                    val userId = jwt.getClaim("user_id").asString()
                    val name = jwt.getClaim("name").asString()
                    val email = jwt.getClaim("email").asString()

                    val prefs = context.getSharedPreferences(UserData.NAME, 0)
                    prefs.edit().putString(UserData.FIELDS.LAST_USER_TOKEN,token).apply()
                    prefs.edit().putString(UserData.FIELDS.LAST_USER_ID, userId).apply()
                    val person = Person(userId, name, email)

                    val db = LocalDatabaseAPI(context)
                    if (db.isPersonExistInDatabase(person!!))
                        db.updatePersonInDatabase(person!!)
                    else
                        db.insertPersonInDatabase(person)


                }
                override fun onFailure(call: Call?, e: IOException?) {

                    errorNotification()
                    return
                }

            })

        } catch (e: ApiException) {
            (context as SignInActivity).signInButton.isEnabled = true
            snackbarNotification.show(context.getString(R.string.autorization_failed))
        }
    }

    fun errorNotification()
    {
        if (!Utilities.isInternetConnected(context))
            snackbarNotification.show(context.getString(R.string.no_internet_connection_available))
        else
            snackbarNotification.show(context.getString(R.string.autorization_failed))
        context.runOnUiThread {
            (context as SignInActivity).signInButton.isEnabled = true
        }
    }
}
