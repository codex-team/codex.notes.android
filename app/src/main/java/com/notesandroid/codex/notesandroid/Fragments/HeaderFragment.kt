package com.notesandroid.codex.notesandroid.Fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.notesandroid.codex.notesandroid.Activities.MainActivity
import com.notesandroid.codex.notesandroid.Essences.User
import com.notesandroid.codex.notesandroid.IMAGES_DIRECTORY
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.SharedPreferenceDatabase.UserData
import kotlinx.android.synthetic.main.nav_header_main.view.*

/**
 *
 * Created by AksCorp on 11.03.2018.
 */
class HeaderFragment : Fragment() {

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.nav_header_main, container, false)

        val user = arguments!!["user"] as User

        val profileIcon =
            Drawable.createFromPath(
                context?.applicationInfo!!.dataDir + "/" + IMAGES_DIRECTORY +
                    "/" +
                    user.profileIconName
            )
        view.profile_image.setImageDrawable(profileIcon)

        // init last sync label
        val lastSyncTime = (context as MainActivity).sharedPreferences.getString(
            UserData.FIELDS.LAST_SYNC,
            ""
        )
        if (!lastSyncTime.isEmpty())
            view.last_sync.text = getString(R.string.last_sync_was_at) + " " + lastSyncTime

        // init user profile name
        if (user.info?.name != null)
            view.user_header_name.text = user.info?.name

        return view
    }

    override fun onDestroy() {
        Log.i("HeaderFragment", "HeaderFragment is destroyed")
        super.onDestroy()
    }
}