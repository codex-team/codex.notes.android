package com.notesandroid.codex.notesandroid.NoteStructure

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v4.content.res.ResourcesCompat
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.notesandroid.codex.notesandroid.R

/**
 * Note block types
 */
val PARAGRAPH_BLOCK = "paragraph"
val HEADER_BLOCK = "header"

/**
 * @param text - current block content
 * @param type - content type. For example it can be [H1] for [HEADER_BLOCK]
 */
open class NoteDescription(var text: String = "", var type: String = "")

/**
 * This class describe any note block.
 *
 * @param context - parent context
 * @param type - block type. For example [PARAGRAPH_BLOCK]
 * @param data - see [NoteDescription]
 */
class NoteBlock(val context: Context, var type: String, var data: NoteDescription) {

    /**
     * View with markup content
     */
    lateinit var view: View

    init {
        when (type.toLowerCase()) {
            PARAGRAPH_BLOCK -> {
                view = ParagraphBlock(context, data).view
            }
            HEADER_BLOCK -> {
                view = HeaderBlock(context, data).view
            }
        }
    }
}

/**
 * Header block types
 */
val H1 = "H1"
val H2 = "H2"
val H3 = "H3"
val H4 = "H4"

/**
 * @param context - parent cotext
 * @param noteDescription - see [NoteDescription]
 */
class HeaderBlock(val context: Context, val noteDescription: NoteDescription) {

    /**
     * see [NoteBlock.view]
     */
    var view: View = getHeaderByType(noteDescription.type)

    /**
     * Markup adjustment for TextView
     *
     * @param type - see [NoteDescription.type]
     */
    @SuppressLint("ResourceType")
    private fun getHeaderByType(type: String): TextView {
        val textView = TextView(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(noteDescription.text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            textView.text = Html.fromHtml(noteDescription.text)
        }
        textView.setPadding(0, 0, 0, 35)
        textView.setTextColor(Color.BLACK)

        // set font from resources
        val array =
            context.obtainStyledAttributes(R.font.pt_serif_web_bold, R.styleable.TextAppearance)
        if (array.hasValue(R.styleable.TextAppearance_android_fontFamily)) {
            val fontId = array.getResourceId(R.styleable.TextAppearance_android_fontFamily, -1)
            val typeface = ResourcesCompat.getFont(context, fontId)
            textView.typeface = typeface
        }

        when (type.toUpperCase()) {
            H1 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
                return textView
            }
            H2 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26F)
                return textView
            }
            H3 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22F)
                return textView
            }
            H4 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)
                return textView
            }
            else -> {
                return textView
            }
        }
    }
}

/**
 * Block with plain text
 *
 * @param context - parent context
 * @param noteDescription - see [NoteDescription]
 */
class ParagraphBlock(val context: Context, val noteDescription: NoteDescription) {

    /**
     * see [NoteBlock.view]
     */
    var view: View = getParagraph()

    /**
     * Markup adjustment for TextView
     */
    private fun getParagraph(): TextView {
        val textView = TextView(context)
        textView.setTextColor(Color.BLACK)

        // set line height
        textView.setLineSpacing(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 6f, context.resources.displayMetrics), 1.0f)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(noteDescription.text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            textView.text = Html.fromHtml(noteDescription.text)
        }

        return textView
    }
}