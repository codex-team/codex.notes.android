package com.notesandroid.codex.notesandroid.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.notesandroid.codex.notesandroid.R
import kotlinx.android.synthetic.main.root_fragment.view.*
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.notesandroid.codex.notesandroid.Essences.Folder
import com.notesandroid.codex.notesandroid.Essences.Note
import com.notesandroid.codex.notesandroid.RVAdapters.NotesAdapter
import org.jetbrains.anko.toast

/**
 * RV with notes by pattern [R.layout.root_fragment]
 *
 * Created by AksCorp on 11.03.2018.
 */
class NotesListFragment : Fragment() {
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.root_fragment, container, false)
        view.root_notes.addItemDecoration(DividerItemDecoration(activity!!,
                DividerItemDecoration.VERTICAL))
        
        if (arguments != null) {
            val folder = arguments!!["folder"] as Folder
            view.root_notes.layoutManager = LinearLayoutManager(activity)
            if (folder.notes != null)
                view.root_notes.adapter = NotesAdapter(folder.notes!!, {})
        }
        
        return view
    }
}