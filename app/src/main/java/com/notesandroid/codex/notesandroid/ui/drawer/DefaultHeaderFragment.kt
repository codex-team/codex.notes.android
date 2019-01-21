package com.notesandroid.codex.notesandroid.ui.drawer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    override fun onDestroy() {
        Log.i("DefaultHeaderFragment", "DefaultHeaderFragment is destroyed")
        super.onDestroy()
    }
}