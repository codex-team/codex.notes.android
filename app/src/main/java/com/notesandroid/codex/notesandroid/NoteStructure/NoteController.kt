package com.notesandroid.codex.notesandroid.NoteStructure

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.util.TypedValue
import android.view.TextureView
import android.view.View
import android.widget.TextView


val PARAGRAPH_BLOCK = "paragraph"
val HEADER_BLOCK = "header"


open class NoteDescription(var text: String = "", var type: Int = -1)

abstract class NoteBlock(
    context: Context,
    var type: String,
    open var data: NoteDescription) {
    lateinit var view: View


    init {
        when(type.toLowerCase())
        {
            PARAGRAPH_BLOCK -> {
            }
            HEADER_BLOCK -> {
                view = HeaderBlock(context).view
            }
        }
    }
}




val H2 = 0
val H3 = 1
val H4 = 2
class HeaderBlock(val context: Context) : NoteDescription() {

    var view: View = getHeaderByType(super.type)

    private fun getHeaderByType(type: Int): TextView {
        val textView = TextView(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            textView.text = Html.fromHtml(text)
        }

        textView.typeface = Typeface.DEFAULT_BOLD

        when (type) {
            H2 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F)

                return textView
            }
            H3 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                return textView
            }
            H4 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
                return textView
            }
            else -> {
                return textView
            }
        }
    }

}

class ParagraphBlock(val context: Context) : NoteDescription() {

    var view: View = getParagraph()

    private fun getParagraph(): TextView {
        val textView = TextView(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            textView.text = Html.fromHtml(text)
        }

        return textView
    }

}


