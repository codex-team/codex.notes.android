package com.notesandroid.codex.notesandroid.Utilities

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import org.jetbrains.anko.runOnUiThread
import java.util.*

/**
 * Created by AksCorp on 24.02.2018.
 */

open class MessageSnackbar(val context: Context, val layout: CoordinatorLayout) {

    fun show(message: String, length: Int = Snackbar.LENGTH_SHORT) {
        context.runOnUiThread {
            val snackbar = Snackbar.make(layout!!, message, length)
            snackbar.show()
        }
    }
}