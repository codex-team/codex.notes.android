package com.notesandroid.codex.notesandroid.data

/**
 * Created by AksCorp on 31.01.2018.
 */
public class Content(
  val folders: MutableList<Folder> = mutableListOf(),
  var rootFolder: Folder? = null
)