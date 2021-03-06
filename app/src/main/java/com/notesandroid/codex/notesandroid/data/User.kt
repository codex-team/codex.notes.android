package com.notesandroid.codex.notesandroid.data

import java.io.Serializable

/**
 * Created by AksCorp on 01.02.2018.
 */
data class User(
  val info: Person? = null,
  val jwt: String? = null,
  val profileIconName: String? = null
) : Serializable