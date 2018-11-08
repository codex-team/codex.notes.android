package com.notesandroid.codex.notesandroid.Activities

import android.annotation.TargetApi
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.notesandroid.codex.notesandroid.ANDROID_CLIENT_ID
import com.notesandroid.codex.notesandroid.ControlUserData
import com.notesandroid.codex.notesandroid.Database.LocalDatabaseAPI
import com.notesandroid.codex.notesandroid.Essences.Content
import com.notesandroid.codex.notesandroid.Essences.Folder
import com.notesandroid.codex.notesandroid.Essences.User
import com.notesandroid.codex.notesandroid.Fragments.DefaultHeaderFragment
import com.notesandroid.codex.notesandroid.Fragments.HeaderFragment
import com.notesandroid.codex.notesandroid.Fragments.NotesListFragment
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.R.string.*
import com.notesandroid.codex.notesandroid.RVAdapters.FoldersAdapter
import com.notesandroid.codex.notesandroid.SYNC_TIME_FORMAT
import com.notesandroid.codex.notesandroid.SharedPreferenceDatabase.UserData
import com.notesandroid.codex.notesandroid.Utilities.MessageSnackbar
import com.notesandroid.codex.notesandroid.Utilities.Utilities
import com.notesandroid.codex.notesandroid.interactor.NoteInteractor
import com.notesandroid.codex.notesandroid.retrofit.CodeXNotesApi
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_view_menu.*
import kotlinx.coroutines.experimental.Job
import retrofit2.HttpException
import java.io.Serializable
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Request code after starting google sign-in activity and handling response in method onActivityResult.
 */

const val AUTHORIZATION_ATTEMPT = 1488

/**
 * Main activity with nav. View
 *
 */
class MainActivity : AppCompatActivity() {

    /**
     * Current user content for display.
     */
    lateinit var content: Content

    /**
     * Current user essence.
     */
    lateinit var user: User

    /**
     * Local database api.
     */
    private val db = LocalDatabaseAPI(this)

    /**
     * For dispose operation if the activity is paused.
     */
    private var disposableContent: Disposable? = null

    /**
     * Interactor for communication with data layer.
     */
    private val interactor = NoteInteractor()

    /**
     * Local SP api.
     */
    lateinit var sharedPreferences: SharedPreferences

    /**
     * Folders adapter. For changing data set
     */
    val foldersAdapter = FoldersAdapter()

    var toggle:ActionBarDrawerToggle? = null

    /**
     * Snackbar for showing if an error occurred.
     */

    lateinit var snackbar: MessageSnackbar

    /**
     * Coroutine with update content logic.
     *
     * @important
     * This coroutine exemplar need to cancel background loading process for certain user.
     * You must cancel it when user logout [logout].
     */
    private lateinit var currentCoroutine: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(UserData.NAME, 0)

        startInit()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBackPressed() {
        super.onBackPressed()
        /*setResult(Activity.RESULT_CANCELED)
        finishAffinity()*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                main_activity_drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
            /*R.id.folder_toolbar_icon -> {
                toast("Folder clicked")
                return true
            }*/
            R.id.refresh_toolbar_icon -> {
                if (user != User()) {
                    loadContent()
                }
            }
        }
        Log.i("MainActivity", item.title.toString() + " " + item.itemId)
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        disposableContent?.dispose()
    }

    internal fun startInit() {

        snackbar = MessageSnackbar(this, main_activity_coordinator_layout)
        interactor.attachSQL(this)

        loadCurrentUser()

        initFields()

        initStartUI()

        content = Content(mutableListOf())
        displayContent()

        Log.i("MainActivityObserver", Thread.currentThread().id.toString() + " " + Thread.currentThread().name)

        if (user != User()) {
            loadContent()
        }
    }

    private fun initFields() {
        folders_rv.layoutManager = LinearLayoutManager(this)
        folders_rv.adapter = foldersAdapter
    }

    /**
     * Load current user information from database.
     */
    private fun loadCurrentUser() {
        val userId = sharedPreferences.getString(UserData.FIELDS.LAST_USER_ID, "")
        val token = sharedPreferences.getString(UserData.FIELDS.LAST_USER_TOKEN, "")
        val profileIcon = sharedPreferences.getString(UserData.FIELDS.PROFILE_ICON, "")
        if (userId.isEmpty() || token.isEmpty()) {
            user = User()
            return
        }
        user = User(db.getPersonFromDatabase(userId), token, profileIcon)
    }

    /**
     * Getting content from [interactor] in Observable using Schedulers.io() for handling it in another
     * thread.
     */
    private fun loadContent() {
        progress_loader.visibility = View.VISIBLE
        disposableContent = interactor.getPersonContent(user.info!!.id!!, user.jwt!!)
            .doAfterNext{ runOnUiThread{progress_loader.visibility = View.GONE }}
            .doAfterTerminate{ runOnUiThread{progress_loader.visibility = View.GONE }}
            .subscribeOn(Schedulers.io()).subscribe({
            if (it.isOnNext) {
                handleContent(it.value!!)
            } else if (it.isOnError) {
                notificationAboutError(it.error!!)
            }
        }, { error ->
            when (error) {
                is HttpException -> Log.i(MainActivity::class.java.simpleName + "Error", error.code().toString())
                else -> Log.i(MainActivity::class.java.simpleName + "Error", error.message)
            }
            error.printStackTrace()
            runOnUiThread {
                //progress_loader.visibility = View.GONE
                snackbar.show(error.message!!)
            }
        }, { Log.i(MainActivity::class.java.simpleName, "Complete") })
    }

    /**
     * After getting content from observable, Handle it and put on [content] then update ui.
     */

    private fun handleContent(cont: Content){
        cont.folders.forEach {
            Log.i(MainActivity::class.java.simpleName, it.toString())
            it.notes!!.forEach { Log.i(MainActivity::class.java.simpleName, it.title) }
        }
        cont.rootFolder = cont.folders.filter { it.isRoot!! }.getOrNull(0)
        content = cont
        runOnUiThread {
            updateSynchronization()
            displayContent()
        }
    }

    /**
     * Show snack bar and load content from database
     * @param error - An error that occurred during sending request or receiving response.
     */

    private fun notificationAboutError(error:Throwable){
        header_layout.isClickable = true
        Log.i("MainActivity", "error log ${error.message}")
        when (error) {
            is HttpException -> {
                Log.i(MainActivity::class.java.simpleName + "Error2", error.code().toString())
                snackbar.show(error.message())
            }
            is UnknownHostException -> {
                snackbar.show(getString(R.string.no_internet_connection_available))
            }
            else -> {
                Log.i(MainActivity::class.java.simpleName + "Error2", error.message)
                snackbar.show(error.message!!)
            }
        }
        content = interactor.loadPersonContentFromSql()
        runOnUiThread { displayContent() }
    }

    /**
     * After successful loading data from server and synchronized time when getting and loading data
     */

    private fun updateSynchronization(){
        val date =
            SimpleDateFormat(SYNC_TIME_FORMAT).format(Calendar.getInstance().time)
        getSharedPreferences(UserData.NAME, 0).edit()
            .putString(UserData.FIELDS.LAST_SYNC, date).apply()
    }


    /**
     * Initialization main UI component. NavBar, toolbar
     *
     */
    private fun initStartUI() {
        setSupportActionBar(toolbar)
        
        toggle = ActionBarDrawerToggle(
            this,
            main_activity_drawer_layout,
            toolbar,
            navigation_drawer_open,
            navigation_drawer_close
        )
        main_activity_drawer_layout.addDrawerListener(toggle!!)
        toggle!!.syncState()

        
        //work with auth button if current user is empty
        if (user == User())
            appointSignInAction()

        nav_view_logout.setOnClickListener {
            logout()
        }
    }

    /**
     * Initialization UI component by context information
     *
     * //TODO replace fun name
     */
    private fun displayContent() {

        setHeaderFragment()

        //init nav view folder RV

        foldersAdapter.setFolders(content.folders.filter { it.isRoot == false }) {
            showNotesFragment(it, true)
        }
        folders_rv.isNestedScrollingEnabled = false
        
        // init notes from root folder button
        nav_view_my_notes.setOnClickListener {
            if (content.rootFolder != null)
                showNotesFragment(content.rootFolder!!, true)
        }

        nav_view_add_folder.setOnClickListener {

        }

        //init start folder fragment. Use root
        if (content.rootFolder != null)
            showNotesFragment(content.rootFolder!!)
        else
            showNotesFragment(Folder())

        // init notes count in root folder
        val rootNotesCount = content.rootFolder?.notes?.size
        notes_counter.text = (rootNotesCount ?: 0).toString()
        notes_counter.visibility = if(rootNotesCount ?: 0 > 0) View.VISIBLE else View.GONE
    }

    /**
     * Replace current fragment to fragment with folder data
     *
     * @param folder folder essence data for display
     */
    private fun showNotesFragment(folder: Folder, backStack:Boolean = false) {
        val bundle = Bundle()
        bundle.putSerializable("folder", folder as Serializable)
        val fragment = NotesListFragment()
        fragment.arguments = bundle
        navigationToFragment(fragment, R.id.main_activity_constraint_layout, backStack)
        main_activity_drawer_layout.closeDrawer(GravityCompat.START)
    }

    private fun setHeaderFragment() {

        fun getFragment(): Fragment
        {
            return when (user) {
                User() ->
                    DefaultHeaderFragment()
                else ->
                    HeaderFragment()
            }
        }

        val bundle = Bundle()
        bundle.putSerializable("user", user as Serializable)
        var fragment = getFragment()
        fragment.arguments = bundle

        navigationToFragment(fragment, R.id.header_layout)
    }

    /**
     * Clear all current user data
     */
    private fun logout() {
        /**
         * @important [currentCoroutine]
         */
        // currentCoroutine.cancel()
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
        header_layout.setOnClickListener {
            header_layout.isClickable = false
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
            if (task.result.idToken != null)
                CodeXNotesApi().authorization(task.result.idToken!!).doFinally {
                    runOnUiThread {
                        main_activity_drawer_layout.closeDrawer(GravityCompat.START)
                    }
                }.subscribe({ jwt ->
                    Log.i("MainActivityObserver", Thread.currentThread().id.toString() + " " + Thread.currentThread().name)
                    user = ControlUserData(db, this).initUserInformation(jwt)
                    runOnUiThread {
                        loadContent()
                    }
                }, { error ->
                    if (!Utilities.isInternetConnected(this))
                        snackbar.show(getString(R.string.no_internet_connection_available))
                    else
                        snackbar.show(getString(R.string.autorization_failed))
                    runOnUiThread {
                        header_layout.isClickable = true
                    }
                    Log.i("MainActivity", "error log ${error.message}")
                })
        }
    }

    /**
     * Common method for changing all fragments
     */

    public fun navigationToFragment(fragment: Fragment, resource: Int, addToBackStack: Boolean = false){
        val transaction = supportFragmentManager.beginTransaction()
            .replace(resource, fragment)
        if(addToBackStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //menu?.clear()
        return true
    }

    /**
     * Method that reset menu on main toolbar
     */

    public fun showMenu(){
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ResourcesCompat.getColor(resources, R.color.mainColorPrimary, null)))
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        toggle?.isDrawerIndicatorEnabled = true
        //invalidateOptionsMenu()
    }

    /**
     * Method that hidden some elements on toolbar before changing menu
     */

    public fun hiddenMenu(){
        activity_main_navigation.visibility = View.GONE
        toggle?.isDrawerIndicatorEnabled = false
        //activity_main_navigation.menu.itemsSequence().forEach { it.isVisible = false }
        toolbar.context.setTheme(R.style.NoteToolbar)
        //supportActionBar?.set
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //invalidateOptionsMenu()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }
}