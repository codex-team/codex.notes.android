package com.notesandroid.codex.notesandroid.note.structure.blocks

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.Utilities.Utilities
import com.notesandroid.codex.notesandroid.note.structure.ImageDescription
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shiplayer on 27.11.18.
 */

class ImageBlock(
    context: Context,
    val description: ImageDescription
) : NoteBlock(context) {
    private var view = getImage()
    private lateinit var image: ImageView
    private lateinit var progressLoader: ProgressBar

    @SuppressLint("CheckResult")
    private fun getImage(): View {
        val layout = LinearLayout(context)
        val params =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams = params

        val frame = FrameLayout(context)
        frame.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        var paramsContent =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        paramsContent.setMargins(dpToPx(19).toInt(), dpToPx(5).toInt(), dpToPx(19).toInt(), dpToPx(5).toInt())

        progressLoader = ProgressBar(context)
        image = ImageView(context)
        image.layoutParams = paramsContent

        frame.addView(progressLoader)
        frame.addView(image)
        show()

        Observable.just(description.image.url)
            .subscribeOn(Schedulers.io())
            .map { Utilities.getDrawableByUrl(description.image.url) }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                hidden()
            }
            .subscribe {
                image.setImageDrawable(it)
            }

        layout.addView(frame)

        val content = TextView(context)

        paramsContent =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        paramsContent.setMargins(dpToPx(19).toInt(), dpToPx(5).toInt(), dpToPx(19).toInt(), dpToPx(5).toInt())
        content.layoutParams = paramsContent

        content.text = description.content

        val borders = ShapeDrawable()
        borders.shape = RectShape()
        borders.paint.color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(R.color.imageContentBorder)
        } else {
            context.resources.getColor(R.color.imageContentBorder)
        }
        borders.paint.strokeWidth = 4f

        borders.paint.style = Paint.Style.STROKE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            content.background = borders
        } else {
            content.setBackgroundDrawable(borders)
        }

        layout.addView(content)

        return layout
    }

    private fun show() {
        image.visibility = View.INVISIBLE
        progressLoader.visibility = View.VISIBLE
    }

    private fun hidden() {
        image.visibility = View.VISIBLE
        progressLoader.visibility = View.GONE
    }

    override fun getView(): View {
        return view
    }
}