package com.notesandroid.codex.notesandroid.base

import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class BaseDelegate<A, V, P> where V: BaseView, P : BasePresenter<V>, A: BaseActivity{
    operator fun provideDelegate(thisRef: A, prop: KProperty<*>): ReadWriteProperty<A, Fragment>{
        return getDelegate()
    }

    abstract fun getDelegate(): ReadWriteProperty<A, Fragment>
}