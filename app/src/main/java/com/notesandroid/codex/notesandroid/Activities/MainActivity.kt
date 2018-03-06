package com.notesandroid.codex.notesandroid.Activities

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.notesandroid.codex.notesandroid.Essences.Note
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import android.support.v7.widget.LinearLayoutManager
import org.jetbrains.anko.toast
import javax.security.auth.login.LoginException
import com.auth0.android.jwt.JWT
import com.notesandroid.codex.notesandroid.*
import com.notesandroid.codex.notesandroid.Database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.Essences.User
import com.notesandroid.codex.notesandroid.RVAdapters.NotesAdapter
import com.notesandroid.codex.notesandroid.SharedPreferenceDatabase.UserData
import com.notesandroid.codex.notesandroid.Utilities.MessageSnackbar
import com.notesandroid.codex.notesandroid.Utilities.MyLog
import android.os.Build
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.notesandroid.codex.notesandroid.R.string.navigation_drawer_close
import com.notesandroid.codex.notesandroid.R.string.navigation_drawer_open
import kotlinx.android.synthetic.main.navigation_view.*


class MainActivity : AppCompatActivity() {
    
    private val logFile: MyLog = MyLog(this)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBackPressed() {
        this.finishAffinity()
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
    
    private fun loadCurrentUser() {
        val prefs = getSharedPreferences(UserData.NAME, 0)
       
        val userId = prefs.getString(UserData.FIELDS.LAST_USER_ID, "")
        val token = prefs.getString(UserData.FIELDS.LAST_USER_TOKEN, "")
        if (userId.isEmpty() || token.isEmpty())
            return
        ApplicationState.currentUser = User(LocalDatabaseAPI(this).getPersonFromDatabase(userId), JWT(token))
    }
    
    private fun initUI() {
        setSupportActionBar(toolbar)
        
        val toggle = ActionBarDrawerToggle(
                this, main_activity_drawer_layout, toolbar, navigation_drawer_open, navigation_drawer_close)
        main_activity_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }
    
    
}
