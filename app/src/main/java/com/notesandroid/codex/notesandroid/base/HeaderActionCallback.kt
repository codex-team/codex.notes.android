package com.notesandroid.codex.notesandroid.base

import android.app.Activity

interface HeaderActionCallback{
    fun action(call: (Activity) -> Unit)
}