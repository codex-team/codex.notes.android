package com.notesandroid.codex.notesandroid.note.structure

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.JsonElement
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.Utilities.TextFormatter
import com.notesandroid.codex.notesandroid.note.structure.blocks.NoteBlock
import com.notesandroid.codex.notesandroid.spans.HeightSpan
import com.notesandroid.codex.notesandroid.spans.OrderedListSpan

class ListBlock(
    context: Context,
    val data:JsonElement
) : NoteBlock(context){
    override fun getView(): View = getListView()

    private fun getListView(): View {
        val textView = TextView(context)
        textView.setTextColor(Color.BLACK)
        val builder = SpannableStringBuilder()
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.setMargins(dpToPx(19).toInt(), dpToPx(5).toInt(), dpToPx(19).toInt(), dpToPx(5).toInt())

        textView.layoutParams = param
        textView.typeface = getSameFont(R.font.roboto_regular)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
        if(data.asJsonObject["style"].asString == "ordered") {
            val items = data.asJsonObject["items"].asJsonArray
            for (i in 1..items.size()) {
                val oldLength = builder.length
                //builder.append(TextFormatter.init(context).parse(items[i - 1].asString + "\n"))
                builder.append(TextFormatter.init(context).parse(items[i - 1].asString).append("\n"))
                builder.setSpan(HeightSpan(25), oldLength, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                builder.setSpan(OrderedListSpan(40, 40, i), oldLength, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        textView.text = builder
        return textView
    }


}