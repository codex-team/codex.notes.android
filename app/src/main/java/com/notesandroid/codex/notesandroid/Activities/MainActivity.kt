package com.notesandroid.codex.notesandroid.Activities

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
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
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.notesandroid.codex.notesandroid.Autorization.ServerSideAutorization
import com.notesandroid.codex.notesandroid.Utilities.MessageSnackbar
import kotlinx.android.synthetic.main.nav_header_main.*
import android.support.v7.widget.LinearLayoutManager
import com.notesandroid.codex.notesandroid.Essences.Folder
import com.notesandroid.codex.notesandroid.Fragments.NotesListFragment
import com.notesandroid.codex.notesandroid.RVAdapters.FoldersAdapter
import kotlinx.android.synthetic.main.nav_view_menu.*
import java.io.Serializable
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.R.string.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

const val AUTHORIZATION_ATTEMPT = 1488

/**
 * Main activity with nav. View
 *
 */
class MainActivity : AppCompatActivity() {
    
    /**
     * Control auth process
     */
    private lateinit var serversideAutorization: ServerSideAutorization
    
    /**
     * Current user content for display
     */
    lateinit var content: Content
    
    /**
     * Current user essence
     */
    lateinit var user: User
    
    /**
     * Local database api
     */
    private val db = LocalDatabaseAPI(this)
    
    /**
     * Local SP api
     */
    private lateinit var sharedPreferences: SharedPreferences
    
    /**
     * Coroutine with update content logic
     *
     * @important
     * This coroutine exemplar need to cancel background loading process for certain user.
     * You must cancel it when user logout [logout]
     */
    private lateinit var currentCoroutine: Job
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        sharedPreferences = getSharedPreferences(UserData.NAME, 0)
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
                main_activity_drawer_layout.openDrawer(GravityCompat.START)
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
        startInit()
    }
    
    internal fun startInit() {
        loadCurrentUser()
        
        initStartUI()
        
        currentCoroutine = launch(CommonPool) {
            content = ControlUserData(db, applicationContext).getContentFromDatabase()
            
            runOnUiThread {
                displayContent()
            }
            if (user.info != null) {
                SaveDataFromServer(db, this@MainActivity).loadContent(user, {
                    runOnUiThread {
                        content = ControlUserData(db, applicationContext).getContentFromDatabase()
                        displayContent()
                    }
                    
                })
            }
        }
    }
    
    /**
     * Load current user information from database
     */
    private fun loadCurrentUser() {
        val userId = sharedPreferences.getString(UserData.FIELDS.LAST_USER_ID, "")
        val token = sharedPreferences.getString(UserData.FIELDS.LAST_USER_TOKEN, "")
        val profileIcon = sharedPreferences.getString(UserData.FIELDS.PROFILE_ICON, "")
        if (userId.isEmpty() || token.isEmpty()) {
            user = User()
            sign_in_button.isEnabled = true
            return
        }
        sign_in_button.isEnabled = false
        user = User(db.getPersonFromDatabase(userId), JWT(token), profileIcon)
    }
    
    /**
     * Initialization main UI component. NavBar, toolbar
     *
     */
    private fun initStartUI() {
        setSupportActionBar(toolbar)
        
        val toggle = ActionBarDrawerToggle(
                this, main_activity_drawer_layout, toolbar, navigation_drawer_open, navigation_drawer_close)
        main_activity_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        
        //work with auth button if current user is empty
        if (user == User())
            appointSignInAction()
    }
    
    /**
     * Initialization UI component by context information
     *
     * //TODO replace fun name
     */
    private fun displayContent() {
        
        //init profile icon
        if (user.profileIconName != null) {
            val profileIcon = Drawable.createFromPath(applicationInfo.dataDir + "/" + IMAGES_DIRECTORY + "/" + user.profileIconName)
            profile_image.setImageDrawable(profileIcon)
        } else {
            profile_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_account_circle))
        }
        
        //init nav view folder RV
        folders_rv.layoutManager = LinearLayoutManager(this)
        folders_rv.adapter = FoldersAdapter(content.folders.filter { it.isRoot == false }, {
            showNotesFragment(it)
        })
        
        // init notes from root folder button
        nav_view_my_notes.setOnClickListener {
            if (content.rootFolder != null)
                showNotesFragment(content.rootFolder!!)
        }
        
        //init start folder fragment. Use root
        if (content.rootFolder != null)
            showNotesFragment(content.rootFolder!!)
        else
            showNotesFragment(Folder())
        
        //init notes count in root folder
        val rootNotesCount = content.rootFolder?.notes?.size
        notes_counter.text = (rootNotesCount ?: 0).toString()
        
        //init last sync label
        val lastSyncTime = sharedPreferences.getString(UserData.FIELDS.LAST_SYNC, "")
        if (lastSyncTime.isEmpty())
            last_sync.text = getString(R.string.not_sync_yet)
        else
            last_sync.text = getString(R.string.last_sync_was_at) + " " + lastSyncTime
        
        //init user profile name
        if (user.info?.name != null)
            user_header_name.text = user.info?.name
        else
            user_header_name.text = getString(R.string.default_user_name)
    }
    
    /**
     * Replace current fragment to fragment with folder data
     *
     * @param folder folder essence data for display
     */
    private fun showNotesFragment(folder: Folder) {
        var fragmentManager = supportFragmentManager
        
        val bundle = Bundle()
        bundle.putSerializable("folder", folder as Serializable)
        val fragment = NotesListFragment()
        fragment.arguments = bundle
        fragmentManager.beginTransaction().replace(R.id.main_activity_constraint_layout, fragment).commit()
        main_activity_drawer_layout.closeDrawer(GravityCompat.START)
    }
    
    /**
     * Clear all current user data
     */
    private fun logout() {
        /**
         * @important [currentCoroutine]
         */
        currentCoroutine.cancel()
        sharedPreferences.edit().clear().apply()
        db.deleteDatabase()
        content = Content()
        user = User()
        startInit()
    }
    
    /**
     * Show signIn dialog after sign in button press
     */
    private fun appointSignInAction() {
        
        serversideAutorization = ServerSideAutorization(this, MessageSnackbar(this, main_activity_coordinator_layout))
        
        sign_in_button.setOnClickListener {
            sign_in_button.isEnabled = false
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(ANDROID_CLIENT_ID)
                    .requestEmail()
                    .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            mGoogleSignInClient.signOut()
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, AUTHORIZATION_ATTEMPT)
        }
        
        nav_view_logout.setOnClickListener {
            logout()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == AUTHORIZATION_ATTEMPT) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            serversideAutorization.handleSignInResult(task, db)
        }
    }
}
