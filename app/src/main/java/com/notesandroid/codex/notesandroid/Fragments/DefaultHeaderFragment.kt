package com.notesandroid.codex.notesandroid.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.notesandroid.codex.notesandroid.R

/**
 *
 * Created by AksCorp on 11.03.2018.
 */
class DefaultHeaderFragment : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.nav_header_default, container, false)
    }
}