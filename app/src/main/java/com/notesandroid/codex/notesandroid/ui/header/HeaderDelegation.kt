package com.notesandroid.codex.notesandroid.ui.header

import android.app.Activity
import androidx.fragment.app.Fragment
import com.notesandroid.codex.notesandroid.ui.drawer.DefaultHeaderFragment
import com.notesandroid.codex.notesandroid.ui.drawer.HeaderFragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class HeaderDelegation() {
    operator fun provideDelegate(thisRef: Activity, prop: KProperty<*>): ReadOnlyProperty<Activity, Fragment> {
        return object : ReadOnlyProperty<Activity, Fragment> {
            override fun getValue(thisRef: Activity, property: KProperty<*>): Fragment {
                if(getPresenter().checkIfLogin())
                    return HeaderFragment.getInstance(getPresenter().getUser()!!)
                else
                    return DefaultHeaderFragment.getInstance()
            }
        }
    }

    abstract fun getPresenter() : HeaderPresenter
}