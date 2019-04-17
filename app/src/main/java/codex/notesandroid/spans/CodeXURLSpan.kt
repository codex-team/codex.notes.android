package codex.notesandroid.spans

import android.text.TextPaint
import android.text.style.URLSpan

/**
 * Created by Shiplayer on 27.11.18.
 */

class CodeXURLSpan(url: String, var foregroundColor: Int) : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint?) {
        super.updateDrawState(ds)
        if (ds != null) {
            ds.isUnderlineText = false
            ds.color = foregroundColor
        }
    }
}