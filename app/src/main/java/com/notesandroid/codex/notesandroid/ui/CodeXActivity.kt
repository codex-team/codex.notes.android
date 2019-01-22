package com.notesandroid.codex.notesandroid.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.loaders.NoteDatabaseLoader
import com.notesandroid.codex.notesandroid.presenter.CodeXPresenter
import com.notesandroid.codex.notesandroid.presenter.NetworkPresenter
import com.notesandroid.codex.notesandroid.presenter.NoteDatabasePresenter
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Activity created on MVP architecture with loader that can provide presenter even if the configuration changes
 */
class CodeXActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<NoteDatabasePresenter>{
    /**
     * Скорее всего, буду использовать один презентер, а все остальные пойдут как провайдеры, для пердоставления данных
     */
    private lateinit var mainPresenter: CodeXPresenter

    /**
     * Field that can communication with database
     */
    private lateinit var presenter: NoteDatabasePresenter

    /**
     * For communication REST API
     */
    private lateinit var networkPresenter: NetworkPresenter

    /**
     *
     */
    private lateinit var toggle: ActionBarDrawerToggle

    /**
     * We created loader then our activity created (in while changing configuration, we get old instance of presenter
     */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<NoteDatabasePresenter> = NoteDatabaseLoader(this)

    /**
     * After loading we can get instance of presenter (if configuration was changed, we can still old instance)
     */
    override fun onLoadFinished(loader: Loader<NoteDatabasePresenter>, data: NoteDatabasePresenter?) {
        if(data != null) {
            presenter = data
        }
    }

    override fun onLoaderReset(loader: Loader<NoteDatabasePresenter>) {
    }

    private val idOfNoteDatabaseLoader = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        LoaderManager.getInstance(this).initLoader(idOfNoteDatabaseLoader, null, this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        toggle = ActionBarDrawerToggle(this, main_activity_drawer_layout, toolbar,R.string.drawer_open, R.string.drawer_close)
        toggle.isDrawerIndicatorEnabled = true
        main_activity_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.refresh_toolbar_icon){
            mainPresenter.refresh()
        }
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }
}