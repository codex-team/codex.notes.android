package com.notesandroid.codex.notesandroid.note.structure.blocks

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.notesandroid.codex.notesandroid.note.structure.ImageDescription

/**
 * Created by Shiplayer on 27.11.18.
 */

class ImageBlock(
    context: Context,
    description: ImageDescription
) : NoteBlock(context) {
    private var view = getImage()
    private lateinit var image: ImageView
    private lateinit var progressLoader: ProgressBar

    private fun getImage(): View {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        val frame = FrameLayout(context)

        progressLoader = ProgressBar(context)

        image = ImageView(context)

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