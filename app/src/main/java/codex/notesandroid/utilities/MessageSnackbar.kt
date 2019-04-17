package codex.notesandroid.utilities

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.runOnUiThread

/**
 * Created by AksCorp on 24.02.2018.
 */

open class MessageSnackbar(
  val context: Context,
  val layout: CoordinatorLayout
) {

    fun show(
      message: String,
      length: Int = Snackbar.LENGTH_SHORT
    ) {
        context.runOnUiThread {
            val snackbar = Snackbar.make(layout!!, message, length)
            snackbar.show()
        }
    }
}