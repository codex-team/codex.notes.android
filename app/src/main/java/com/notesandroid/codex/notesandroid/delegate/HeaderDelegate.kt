package com.notesandroid.codex.notesandroid.delegate

import androidx.fragment.app.Fragment
import com.notesandroid.codex.notesandroid.base.HeaderActionCallback
import com.notesandroid.codex.notesandroid.base.IDelegate
import com.notesandroid.codex.notesandroid.ui.drawer.DefaultHeaderFragment
import com.notesandroid.codex.notesandroid.ui.drawer.HeaderFragment
import com.notesandroid.codex.notesandroid.ui.header.HeaderDelegatePresenter
import kotlin.reflect.KProperty

class HeaderDelegate(val action: HeaderActionCallback) : IDelegate<Fragment> {
    private val presenter by lazy {
        HeaderDelegatePresenter()
    }

    override fun getValue(thisRef: Any, prop: KProperty<*>): Fragment {
        val user = presenter.getUser()
        return if(user == null){
            DefaultHeaderFragment.getInstance()
        } else {
            HeaderFragment.getInstance(user)
        }
    }

    override fun initPresenters() {
        presenter.connectToDelegate {
            action.update()
        }
    }

    override fun doOnProvide() {

    }
}