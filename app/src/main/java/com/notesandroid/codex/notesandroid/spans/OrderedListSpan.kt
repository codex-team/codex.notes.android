package com.notesandroid.codex.notesandroid.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan

/**
 * Style of ordered list
 * @param grapWidth - left padding
 * @param leadWidth - padding right
 * @param index - index of list
 */
class OrderedListSpan(val grapWidth:Int, val leadWidth:Int, val index:Int) : LeadingMarginSpan{
    override fun drawLeadingMargin(canvas: Canvas?, paint: Paint?, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence?, start: Int, end: Int, first: Boolean, l: Layout?) {
        if(first){
            val oldStyle = paint!!.style
            paint.style = Paint.Style.FILL
            val width = paint.measureText("$index")
            canvas!!.drawText("$index", (leadWidth + x - width / 2) * dir, baseline.toFloat(), paint)
            paint.style = oldStyle
        }
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return grapWidth + leadWidth
    }

}