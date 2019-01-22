package com.notesandroid.codex.notesandroid.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.style.ReplacementSpan
import android.util.Log

/**
 * Created by Shiplayer on 22.11.18.
 */

/**
 * InlineStyleSpan class for styling inline code
 * @param backgroundColor - Background color for that text
 * @param foregroundColor - Color for text
 */
class InlineStyleSpan(
    val backgroundColor: Int,
    val foregroundColor: Int
) : ReplacementSpan() {
    private val rectF = RectF()
    private val PADDING = 20f

    /**
     * Method for calculating width of text
     */
    override fun getSize(
        paint: Paint?,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val style = paint?.style
        Log.i("InlineStyleSpan", "Is paint monospace? " + paint?.typeface?.equals(Typeface.MONOSPACE))
        paint!!.typeface = Typeface.MONOSPACE
        val measure = Math.round(paint.measureText(text, start, end) + PADDING * 2)
        paint.style = style
        return measure
    }

    /**
     * Draw text, background, foreground, change font family for that text and padding
     */
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
        val style = paint!!.style
        paint.typeface = Typeface.MONOSPACE
        rectF.set(
            x + PADDING / 4,
            y + paint.ascent() - PADDING / 2,
            x + paint.measureText(text, start, end) + PADDING,
            bottom.toFloat() + PADDING / 2
        )
        paint.color = backgroundColor
        canvas!!.drawRect(rectF, paint)

        paint.color = foregroundColor
        // paint.typeface = Typeface.MONOSPACE
        val xPos = (x + PADDING / 2)
        val yPos = y.toFloat() // (canvas.height / 2 + (paint.descent() + paint.ascent()) / 2)
        canvas.drawText(text, start, end, xPos, yPos, paint)

        paint.style = style
    }
}