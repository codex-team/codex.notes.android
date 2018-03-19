package com.notesandroid.codex.notesandroid.Essences

import com.auth0.android.jwt.JWT
import java.io.Serializable

/**
 * Created by AksCorp on 01.02.2018.
 */
data class User(val info: Person? = null, val jwt: JWT? = null, val profileIconName: String? = null) : Serializable