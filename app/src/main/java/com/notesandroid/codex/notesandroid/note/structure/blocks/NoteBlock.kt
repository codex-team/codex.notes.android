package com.notesandroid.codex.notesandroid.note.structure.blocks

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Build
import android.support.v4.content.res.ResourcesCompat
import android.view.View

/**
 * This class describe any note block.
 *
 * @param context - parent context
 * @param type - block type. For example [PARAGRAPH_BLOCK]
 * @param data - see [NoteDescription]
 */
abstract class NoteBlock(
    val context: Context
) {
    /**
     * abstract method for getting view
     */
    abstract fun getView(): View

    /**
     * For getting font from resources
     * @param fontResource - Id of font in resources
     *
     * @return type that view can converter in current font
     */
    protected fun getSameFont(
        fontResource: Int
    ): Typeface? {
        val sameFont = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.resources.getFont(fontResource)
        } else {
            ResourcesCompat.getFont(context, fontResource)
        }
        return sameFont
    }

    /**
     * Convert dp to px
     */
    protected fun dpToPx(
        dp: Int
    ): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }
}