package com.notesandroid.codex.notesandroid.note.structure.blocks

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.notesandroid.codex.notesandroid.note.structure.NoteDescription
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.Utilities.TextFormatter
import com.notesandroid.codex.notesandroid.spans.HeightSpan

/**
 * Header block types
 */
val H1 = "H1"
val H2 = "H2"
val H3 = "H3"
val H4 = "H4"

/**
 * Block with plain text
 *
 * @param context - parent context
 * @param noteDescription - see [NoteDescription]
 */
class ParagraphBlock(
    context: Context,
    val noteDescription: NoteDescription
) : NoteBlock(context) {
    override fun getView(): View {
        return view
    }

    /**
     * see [getView]
     */
    private var view: View = getParagraph()

    /**
     * Markup adjustment for TextView
     */
    private fun getParagraph(): TextView {
        val textView = TextView(context)
        textView.setTextColor(Color.BLACK)

        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.setMargins(dpToPx(19).toInt(), dpToPx(5).toInt(), dpToPx(19).toInt(), dpToPx(5).toInt())

        textView.layoutParams = param
        textView.typeface = getSameFont(R.font.roboto_regular)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.isClickable = true

        val block = TextFormatter.init(context).parse(noteDescription.text)
        block.setSpan(HeightSpan(25), 0, block.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = block

        return textView
    }
}