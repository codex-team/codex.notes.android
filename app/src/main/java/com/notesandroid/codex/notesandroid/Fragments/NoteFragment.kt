package com.notesandroid.codex.notesandroid.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.Essences.Note
import com.notesandroid.codex.notesandroid.NoteStructure.NoteBlock
import com.notesandroid.codex.notesandroid.NoteStructure.NoteDescription
import com.notesandroid.codex.notesandroid.R
import kotlinx.android.synthetic.main.note.view.*

/**
 * RV with notes by pattern [R.layout.root_fragment]
 *
 * Created by AksCorp on 11.03.2018.
 */
class NoteFragment : Fragment() {

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.note, container, false)
        if (arguments != null) {
            val note = arguments!!["note"] as Note

            val builder = GsonBuilder()
            val gson = builder.create()

            val writer = JsonParser()
            var jsonElement = writer.parse(note.content)
            val jsonArray = jsonElement.asJsonArray

            addTitleToLayout(view, note.title!!)

            for (el in jsonArray) {

                val blockType = el.asJsonObject["type"].asString
                val dataText = el.asJsonObject["data"].asJsonObject["text"].asString

                val dataType = el.asJsonObject["data"].asJsonObject["heading-styles"]

                val block = NoteBlock(context!!, blockType, NoteDescription(dataText, dataType?.asString ?: ""))

              //  elements.add(textElement)

                (view.root_note_linear_layout as LinearLayout).addView(block.view)
            }
        }

        return view
    }

    fun addTitleToLayout(view: View, title: String) {

        val blockType = "header"
        val dataText = title

        val dataType = "h1"

        val block = NoteBlock(context!!, blockType, NoteDescription(dataText, dataType))

        (view.root_note_linear_layout as LinearLayout).addView(block.view)
    }
}