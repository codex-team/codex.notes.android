package com.notesandroid.codex.notesandroid.ui.drawer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.notesandroid.codex.notesandroid.ui.MainActivity
import com.notesandroid.codex.notesandroid.data.Folder
import com.notesandroid.codex.notesandroid.ui.NoteFragment
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.adapters.NotesAdapter
import kotlinx.android.synthetic.main.root_fragment.view.*
import java.io.Serializable

/**
 * RV with notes by pattern [R.layout.root_fragment]
 *
 * Created by AksCorp on 11.03.2018.
 */
class NotesListFragment : Fragment() {

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.root_fragment, container, false)
        view.root_notes.addItemDecoration(
            DividerItemDecoration(
                activity!!,
                DividerItemDecoration.VERTICAL
            )
        )

        if (arguments != null) {
            val folder = arguments!!["folder"] as Folder
            view.root_notes.layoutManager = LinearLayoutManager(activity)
            if (folder.notes != null) {
                folder.notes = folder.notes!!.filter {
                    !it.title!!.isEmpty()
                }.sortedByDescending{ it.dtModify!!.toInt() }.toMutableList()
                if (folder.notes != null)
                    view.root_notes.adapter = NotesAdapter(folder.notes!!) { note ->

                        val bundle = Bundle()
                        bundle.putSerializable("note", note as Serializable)
                        val fragment = NoteFragment()
                        fragment.arguments = bundle

                        (context as MainActivity).navigationToFragment(fragment, R.id.main_activity_constraint_layout, true)
                    }
            }
        }

        return view
    }

    override fun onDestroy() {
        Log.i("NoteListFragment", "is destroyed")
        super.onDestroy()
    }
}