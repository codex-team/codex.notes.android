package com.notesandroid.codex.notesandroid.ui.header

import android.app.Activity
import androidx.fragment.app.Fragment
import com.notesandroid.codex.notesandroid.base.BasePresenter
import com.notesandroid.codex.notesandroid.base.BaseView
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class HeaderDelegation<V : BaseView, P : BasePresenter<V>>() {
    operator fun provideDelegate(thisRef: Activity, prop: KProperty<*>): ReadWriteProperty<Activity, Fragment> {
        return object : ReadWriteProperty<Activity, Fragment> {
            override fun getValue(thisRef: Activity, property: KProperty<*>): Fragment {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun setValue(thisRef: Activity, property: KProperty<*>, value: Fragment) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    abstract fun getPresenter() : P
}