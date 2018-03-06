package com.notesandroid.codex.notesandroid.Utilities

import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DialogTitle
import android.widget.Toast
import org.jetbrains.anko.runOnUiThread
import java.util.*
import javax.security.auth.callback.Callback

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