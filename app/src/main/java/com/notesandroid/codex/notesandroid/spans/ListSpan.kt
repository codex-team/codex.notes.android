package com.notesandroid.codex.notesandroid.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import com.notesandroid.codex.notesandroid.note.structure.blocks.NoteBlock

/**
 * Style for ordered or unordered list
 * @param grapWidth - left padding
 * @param leadWidth - padding right
 * @param index - index of list, may be null, if null then return unordered list
 */
class ListSpan(val grapWidth:Int, val leadWidth:Int, val index:Int? = null) : LeadingMarginSpan{
    override fun drawLeadingMargin(canvas: Canvas?, paint: Paint?, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence?, start: Int, end: Int, first: Boolean, l: Layout?) {
        if(first){
            val oldStyle = paint!!.style
            paint.style = Paint.Style.FILL
            if(index != null) {
                val width = paint.measureText("$index")
                canvas!!.drawText("$index", (leadWidth + x - width / 2) * dir, baseline.toFloat(), paint)
            } else {
                paint.color = Color.BLACK
                canvas!!.drawCircle((leadWidth + x - NoteBlock.dpToPx(2) / 2), (bottom - (bottom - top) / 2 + NoteBlock.dpToPx(2)), NoteBlock.dpToPx(2), paint)
            }
            paint.style = oldStyle
        }
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return grapWidth + leadWidth
    }

}