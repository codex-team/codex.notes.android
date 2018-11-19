package com.notesandroid.codex.notesandroid.Essences

import java.io.Serializable

/**
 * Created by AksCorp on 31.01.2018.
 */
data class Folder(
  var id: String? = null,
  var title: String? = null,
  var owner: Person? = null,
  var isRoot: Boolean? = null,
  var notes: MutableList<Note>? = null
) : Serializable