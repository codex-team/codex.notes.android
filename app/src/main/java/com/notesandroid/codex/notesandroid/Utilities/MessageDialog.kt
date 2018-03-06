package com.notesandroid.codex.notesandroid.Utilities

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DialogTitle
import android.widget.Toast
import org.jetbrains.anko.runOnUiThread
import java.util.*
import javax.security.auth.callback.Callback

/**
 * Created by AksCorp on 24.02.2018.
 */

open class MessageDialog(val context: Context) {

    interface Callback {
        fun run(dialog: DialogInterface, i: Int)
    }

    fun show(title: String, message: String, positiveButton: Pair<String, Callback>? = null, negativeButton: Pair<String, Callback>? = null) {
        context.runOnUiThread {
            if (positiveButton == null && negativeButton == null)
                createMessageDialog(title, message)?.show()
            if (positiveButton != null)
                createMessageDialog(title, message, positiveButton)?.show()
            if (positiveButton != null && negativeButton != null)
                createMessageDialog(title, message, positiveButton, negativeButton)?.show()
        }
    }

    private fun createMessageDialog(title: String, message: String): AlertDialog? {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        return alertDialog
    }

    private fun createMessageDialog(title: String, message: String, positiveButton: Pair<String, Callback>): AlertDialog? {
        var alertDialog = createMessageDialog(title, message)

        alertDialog?.setButton(AlertDialog.BUTTON_POSITIVE, positiveButton.first, { dialogInterface, i ->
            positiveButton.second.run(dialogInterface, i)
        })

        return alertDialog
    }

    private fun createMessageDialog(title: String, message: String, positiveButton: Pair<String, Callback>, negativeButton: Pair<String, Callback>): AlertDialog? {
        var alertDialog = createMessageDialog(title, message, positiveButton)

        alertDialog?.setButton(AlertDialog.BUTTON_NEGATIVE, negativeButton.first, { dialogInterface, i ->
            negativeButton.second.run(dialogInterface, i)
        })

        return alertDialog
    }
}