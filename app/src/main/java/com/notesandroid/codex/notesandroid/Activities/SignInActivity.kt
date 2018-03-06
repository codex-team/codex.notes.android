package com.notesandroid.codex.notesandroid.Activities

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.notesandroid.codex.notesandroid.R
import kotlinx.android.synthetic.main.sign_in.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.notesandroid.codex.notesandroid.ANDROID_CLIENT_ID
import com.notesandroid.codex.notesandroid.Autorization.ServersideAutorization
import com.notesandroid.codex.notesandroid.Utilities.MessageSnackbar


class SignInActivity : AppCompatActivity() {
    
    val REQUEST_CODE = 200
    
    lateinit var serversideAutorization: ServersideAutorization
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)
        
        serversideAutorization = ServersideAutorization(this, MessageSnackbar(this, sign_in_activity_coordinator_layout))
        
        signInButton.setOnClickListener {
            signInButton.isEnabled = false
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(ANDROID_CLIENT_ID)
                    .requestEmail()
                    .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            mGoogleSignInClient.signOut()
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, REQUEST_CODE)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            serversideAutorization.handleSignInResult(task)
        }
    }
}
