package codex.notesandroid.spans

import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.text.style.LineHeightSpan
import kotlin.math.ceil

/**
 * Created by Shiplayer on 22.11.18.
 */

/**
 * HeightSpan for set up for all text height line
 * @param mSize - size of height line (may be in pixels)
 */
class HeightSpan(
    val mSize: Int
) : LineHeightSpan.WithDensity {
    var sProportion = 0f

    /**
     * Method for calculation height for each line
     */
    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        v: Int,
        fm: Paint.FontMetricsInt?,
        paint: TextPaint?
    ) {
        var size = mSize.toFloat()
        if (paint != null) {
            size *= paint.density
        }
        if (fm!!.bottom - fm.top < size) {
            fm.top = (fm.bottom - size).toInt()
            fm.ascent = (fm.descent - size).toInt()
            if (paint != null) {
                fm.bottom = (fm.bottom + 2 * paint.density).toInt()
            } else {
                fm.bottom = fm.bottom + 2
            }
        } else {
            if (sProportion == 0f) {
                val p = Paint()
                p.textSize = 100f
                val r = Rect()
                p.getTextBounds("ABCDEFG", 0, 7, r)
                sProportion = (r.top) / p.ascent()
            }
            val need = ceil(-fm.top * sProportion)
            if (size - fm.descent >= need) {
                fm.top = (fm.bottom - size).toInt()
                fm.ascent = (fm.descent - size).toInt()
            } else if (size >= need) {
                fm.top = fm.ascent
                fm.ascent = (-need).toInt()
                fm.bottom = fm.descent
                fm.descent = (fm.top + size).toInt()
            } else {
                fm.top = fm.ascent
                fm.ascent = (-size).toInt()
                fm.bottom = fm.descent
                fm.descent = 0
            }
        }
    }

    /**
     * Some, but without [Paint]
     */
    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        v: Int,
        fm: Paint.FontMetricsInt?
    ) {
        chooseHeight(text, start, end, spanstartv, v, fm, null)
    }
}