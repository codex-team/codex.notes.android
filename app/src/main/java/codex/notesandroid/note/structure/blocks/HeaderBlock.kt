package codex.notesandroid.note.structure.blocks

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import codex.notesandroid.R
import codex.notesandroid.note.structure.NoteDescription

/**
 * @param context - parent cotext
 * @param noteDescription - see [NoteDescription]
 */
class HeaderBlock(
    context: Context,
    private val noteDescription: NoteDescription
) : NoteBlock(context) {

    /**
     * see [NoteBlock.getView]
     */
    private var view: View = getHeaderByType(noteDescription.type)

    /**
     * Markup adjustment for TextView
     *
     * @param type - see [NoteDescription.type]
     */
    @SuppressLint("ResourceType")
    private fun getHeaderByType(
        type: String
    ): TextView {
        val textView = TextView(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(noteDescription.text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            textView.text = Html.fromHtml(noteDescription.text)
        }

        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        textView.setTextColor(Color.BLACK)

        // set font from resources
        val typeface = getSameFont(R.font.pt_serif_web_bold)

        textView.typeface = typeface

        val bottomPadding = when (type.toUpperCase()) {
            H1 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
                dpToPx(12).toInt()
            }
            H2 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F)
                dpToPx(8).toInt()
            }
            H3 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21F)
                dpToPx(6).toInt()
            }
            H4 -> {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)
                dpToPx(5).toInt()
            }
            else ->
                dpToPx(0).toInt()
        }
        param.setMargins(dpToPx(19).toInt(), dpToPx(25).toInt(), dpToPx(19).toInt(), bottomPadding)
        textView.layoutParams = param
        return textView
    }

    override fun getView(): View {
        return view
    }
}