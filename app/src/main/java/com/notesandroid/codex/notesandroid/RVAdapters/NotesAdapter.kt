package com.notesandroid.codex.notesandroid.RVAdapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.notesandroid.codex.notesandroid.Essences.Note
import com.notesandroid.codex.notesandroid.R
import kotlinx.android.synthetic.main.note.view.*

/**
 * Created by AksCorp on 01.02.2018.
 *
 * Recycler view adapter for display notes
 *
 * @param notes list with display notes
 * @param itemClick callback for click RV element
 *
 */
class NotesAdapter(private val notes: List<Note>,
                   private val itemClick: (Note) -> Unit) :
        RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note, parent, false)
        return ViewHolder(view, itemClick)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(notes[position])
    }
    
    override fun getItemCount() = notes.size
    
    class ViewHolder(view: View, private val itemClick: (Note) -> Unit)
        : RecyclerView.ViewHolder(view) {
        
        fun bindForecast(note: Note) {
            itemView.note_title.text = note.title
            itemView.setOnClickListener { itemClick(note) }
        }
    }
}