package com.notesandroid.codex.notesandroid.Fragments

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.Essences.Note
import com.notesandroid.codex.notesandroid.NoteStructure.NoteBlock
import com.notesandroid.codex.notesandroid.NoteStructure.TextElement
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
            
            val elements = mutableListOf<NoteBlock>()
            
            for (el in jsonArray) {
                val textElement = gson.fromJson(el.asJsonObject["data"], NoteBlock::class.java)
                elements.add(textElement)
                
                (view.root_note_linear_layout as LinearLayout).addView(tv)
            }
        }
        
        return view
    }
}