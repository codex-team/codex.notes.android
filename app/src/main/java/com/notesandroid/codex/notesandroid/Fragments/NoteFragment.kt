package com.notesandroid.codex.notesandroid.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.Essences.Note
import com.notesandroid.codex.notesandroid.NoteStructure.NoteBlockFactory
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

                //val block = NoteBlock(context!!, blockType, NoteDescription(dataText, dataType?.asString ?: ""))
                val block = NoteBlockFactory.createBlock(context!!, el.asJsonObject)

              //  elements.add(textElement)

                (view.root_note_linear_layout as LinearLayout).addView(block.getView())
            }
        }

        return view
    }

    fun addTitleToLayout(view: View, title: String) {
        val jsonObj = JsonObject()//"{\"type\": header, \"data\": {\"text\": $title, \"level\": 1} }")
        jsonObj.addProperty("type", "header")
        val jsonElem = JsonObject()
        jsonElem.addProperty("text", title)
        jsonElem.addProperty("level", 1)
        jsonObj.add("data", jsonElem)
        val blockType = "header"
        val dataText = title

        val dataType = "h1"

        val block = NoteBlockFactory.createBlock(context!!, jsonObj)

        (view.root_note_linear_layout as LinearLayout).addView(block.getView())
    }
}