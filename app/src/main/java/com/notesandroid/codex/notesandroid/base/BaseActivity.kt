package com.notesandroid.codex.notesandroid.base

import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager

abstract class BaseActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<BasePresenter<BaseView>> {

}