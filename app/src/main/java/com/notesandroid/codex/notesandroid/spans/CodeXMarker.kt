package com.notesandroid.codex.notesandroid.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.style.ReplacementSpan

/**
 * Created by Shiplayer on 27.11.18.
 */

class CodeXMarker(
    var backgroundColor: Int
) : ReplacementSpan() {
    private val rectF = RectF()
    private val PADDING = 20f

    override fun getSize(paint: Paint?, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return Math.round(paint!!.measureText(text, start, end))
    }

    override fun draw(
        canvas: Canvas?,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint?
    ) {

        paint!!.typeface = Typeface.MONOSPACE
        rectF.set(
            x,
            y + paint.ascent() - PADDING / 4,
            x + paint.measureText(text, start, end),
            bottom.toFloat() + PADDING / 4
        )
        paint.color = backgroundColor
        canvas!!.drawRect(rectF, paint)

        val xPos = (x)
        val yPos = y.toFloat() // (canvas.height / 2 + (paint.descent() + paint.ascent()) / 2)
        canvas.drawText(text, start, end, xPos, yPos, paint)
    }
}