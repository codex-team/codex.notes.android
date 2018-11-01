package com.notesandroid.codex.notesandroid.RVAdapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.notesandroid.codex.notesandroid.Essences.Folder
import com.notesandroid.codex.notesandroid.R
import kotlinx.android.synthetic.main.folder.view.*

/**
 * Created by AksCorp on 01.02.2018.
 *
 * Recycler view adapter for display folders
 *
 * @param folders list with display folders
 * @param itemClick callback for click RV element
 *
 */
class FoldersAdapter(
  private val folders: List<Folder>,
  private val itemClick: (Folder) -> Unit
) :
        RecyclerView.Adapter<FoldersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(folders[position])
    }

    override fun getItemCount() = folders.size

    class ViewHolder(view: View, private val itemClick: (Folder) -> Unit)
        : RecyclerView.ViewHolder(view) {

        fun bindForecast(folder: Folder) {
            itemView.nav_view_folder.text = folder.title
            itemView.setOnClickListener { itemClick(folder) }
        }
    }
}