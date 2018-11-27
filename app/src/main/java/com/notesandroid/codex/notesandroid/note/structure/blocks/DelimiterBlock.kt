package com.notesandroid.codex.notesandroid.note.structure.blocks

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.notesandroid.codex.notesandroid.R

class DelimiterBlock(
    context: Context
) : NoteBlock(context) {
    override fun getView(): View {
        return view
    }

    /**
     * View with markup content
     */
    private var view: View = getDelimiter()

    private fun getDelimiter(): LinearLayout {
        val delimiter = TextView(context)

        val linearLayout = LinearLayout(context)

        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.setMargins(dpToPx(20).toInt(), dpToPx(10).toInt(), dpToPx(19).toInt(), dpToPx(5).toInt())

        delimiter.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        delimiter.typeface = getSameFont(R.font.roboto_regular)
        delimiter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)

        delimiter.text = "* * *"

//        delimiter.letterSpacing = 2f

        linearLayout.addView(delimiter)
        linearLayout.layoutParams = param

        linearLayout.gravity = Gravity.CENTER_HORIZONTAL

        return linearLayout
    }
}