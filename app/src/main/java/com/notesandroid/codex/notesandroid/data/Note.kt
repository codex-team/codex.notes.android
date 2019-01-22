package com.notesandroid.codex.notesandroid.data

import java.io.Serializable

/**
 * Created by AksCorp on 31.01.2018.
 */
data class Note(
  var id: String? = null,
  var folderId: String? = null,
  var title: String? = null,
  var content: String? = null,
  var dtCreate: String? = null,
  var dtModify: String? = null,
  var author: Person? = null,
  var isRemoved: Boolean? = null
) : Serializable