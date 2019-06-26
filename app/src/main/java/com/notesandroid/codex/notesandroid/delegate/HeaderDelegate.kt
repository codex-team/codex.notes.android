package com.notesandroid.codex.notesandroid.delegate

import androidx.fragment.app.Fragment
import com.notesandroid.codex.notesandroid.base.BaseActivity
import com.notesandroid.codex.notesandroid.base.BaseDelegate
import com.notesandroid.codex.notesandroid.ui.header.HeaderPresenter
import com.notesandroid.codex.notesandroid.ui.header.HeaderView
import kotlin.properties.ReadWriteProperty

class HeaderDelegate<A: BaseActivity> : BaseDelegate<A, HeaderView, HeaderPresenter>() {
    override fun getDelegate(): ReadWriteProperty<A, Fragment> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}