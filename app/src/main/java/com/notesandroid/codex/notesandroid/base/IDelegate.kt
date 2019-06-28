package com.notesandroid.codex.notesandroid.base

import kotlin.reflect.KProperty

interface IDelegate<out V> {
    fun initPresenters()

    operator fun getValue(thisRef: Any, prop: KProperty<*>): V

    fun doOnProvide()

    operator fun provideDelegate(thisRef: Any, prop: KProperty<*>): IDelegate<V> {
        initPresenters()
        doOnProvide()
        return this
    }
}