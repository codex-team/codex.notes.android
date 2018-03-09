package com.notesandroid.codex.notesandroid.Activities

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import com.auth0.android.jwt.JWT
import com.notesandroid.codex.notesandroid.*
import com.notesandroid.codex.notesandroid.Database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.Essences.User
import com.notesandroid.codex.notesandroid.SharedPreferenceDatabase.UserData
import com.notesandroid.codex.notesandroid.Utilities.MyLog
import android.os.Build
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.notesandroid.codex.notesandroid.Autorization.ServerSideAutorization
import com.notesandroid.codex.notesandroid.R.string.navigation_drawer_close
import com.notesandroid.codex.notesandroid.R.string.navigation_drawer_open
import com.notesandroid.codex.notesandroid.Utilities.MessageSnackbar
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.navigation_view.*



const val AUTHORIZATION_ATTEMPT = 200


/**
 * Main activity with nav. View
 *
 */
class MainActivity : AppCompatActivity() {
    
    private val logFile: MyLog = MyLog(this)
    
    
    lateinit var serversideAutorization: ServerSideAutorization
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finishAffinity()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
            R.id.folder_toolbar_icon -> {
                toast("Folder clicked")
                return true
            }
        }
        return super.onOptionsItemSelected(item);
    }
    
    override fun onResume() {
        super.onResume()
        try {
            loadCurrentUser()
        } catch (e: Exception) {
            logFile.log(e.toString())
        }
    }
    
    /**
     * Load current user information from database
     */
    private fun loadCurrentUser() {
        val prefs = getSharedPreferences(UserData.NAME, 0)
        
        val userId = prefs.getString(UserData.FIELDS.LAST_USER_ID, "")
        val token = prefs.getString(UserData.FIELDS.LAST_USER_TOKEN, "")
        if (userId.isEmpty() || token.isEmpty())
            return
        ApplicationState.currentUser = User(LocalDatabaseAPI(this).getPersonFromDatabase(userId), JWT(token))
    }
    
    /**
     * Initialization UI component. NavBar, toolbar
     */
    private fun initUI() {
        setSupportActionBar(toolbar)
        
        val toggle = ActionBarDrawerToggle(
                this, main_activity_drawer_layout, toolbar, navigation_drawer_open, navigation_drawer_close)
        main_activity_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        
        //appoint buttons
        appointSignInAction()
    }
    
    /**
     * Show signIn dialog after sign in button press
     */
    private fun appointSignInAction() {
        
        signInButton.setOnClickListener {
            serversideAutorization = ServerSideAutorization(this, MessageSnackbar(this, main_activity_coordinator_layout))
            signInButton.isEnabled = false
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(ANDROID_CLIENT_ID)
                    .requestEmail()
                    .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            mGoogleSignInClient.signOut()
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, AUTHORIZATION_ATTEMPT)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == AUTHORIZATION_ATTEMPT) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            serversideAutorization.handleSignInResult(task)
        }
    }
}
