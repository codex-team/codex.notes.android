package com.notesandroid.codex.notesandroid.Fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.Activities.MainActivity
import com.notesandroid.codex.notesandroid.Essences.Note
import com.notesandroid.codex.notesandroid.NoteStructure.NoteBlockFactory
import com.notesandroid.codex.notesandroid.R
import com.notesandroid.codex.notesandroid.Utilities.DateFormatter
import com.notesandroid.codex.notesandroid.Utilities.Utilities
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.note.view.*

/**
 * RV with notes by pattern [R.layout.root_fragment]
 *
 * Created by AksCorp on 11.03.2018.
 */
class NoteFragment : Fragment() {

    /**
     * Change background color and set custom toolbar
     */

    var note:Note? = null

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        val actionBar = (activity as MainActivity).supportActionBar
        if(actionBar != null)
            initActionBar(actionBar)
    }

    /**
     * initializing action bar and set up information about owner this note
     * @param actionBar action bar that need set up information
     */

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "CheckResult")
    private fun initActionBar(actionBar: ActionBar){
        if(actionBar.customView != null){
            Log.i("NoteFragment", "customView already set")
        }
        actionBar.customView = activity!!.layoutInflater.inflate(R.layout.note_actionbar_layout, null)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        if(note != null){
            actionBar.customView.findViewById<TextView>(R.id.note_person_name).text = note!!.author?.name
            val progressBar = actionBar.customView.findViewById<ProgressBar>(R.id.note_progress_bar)
            val imageView = actionBar.customView.findViewById<CircleImageView>(R.id.note_person_logo)
            actionBar.customView.findViewById<TextView>(R.id.note_last_sync).text = resources.getText(R.string.note_last_edit).toString() +
                " " + DateFormatter.parseDate(note!!.dtModify!!.toLong() * 1000L, resources.getString(R.string.edited_today), resources.getString(R.string.edited_yesterday))
            Single.just(note!!.author!!.photo).doOnSubscribe{
                progressBar.visibility = View.VISIBLE
                imageView.visibility = View.GONE
            }.subscribeOn(Schedulers.io()).map {
                loadDrawableOrDefault(it)
            }.observeOn(AndroidSchedulers.mainThread()).doFinally{
                progressBar.visibility = View.GONE
                imageView.visibility = View.VISIBLE
            }.subscribe ({ file ->
                imageView.setImageDrawable(file)
            }, {error ->
                Log.e("NoteFragmentError", error.message)
            })
            actionBar.customView.findViewById<ImageView>(R.id.note_back_image).setOnClickListener {
                (activity as MainActivity).onBackPressed()
            }
        }
    }

    /**
     * function for downloading logo image
     * @param url photo url
     * @return logo after downloading if occurs error then return default logo image
     */

    private fun loadDrawableOrDefault(url:String): Drawable {
        return when {
            Utilities.isInternetConnected(context!!) -> Utilities.getDrawableByUrl(url)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> resources.getDrawable(R.drawable.ic_google__g__logo, null)
            else -> resources.getDrawable(R.drawable.ic_google__g__logo)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.i("NoteFragment", item!!.title.toString() + " " + item.itemId)
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hiddenMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.note, container, false)
        if (arguments != null) {
            note = arguments!!["note"] as Note


            val builder = GsonBuilder()
            val gson = builder.create()

            val writer = JsonParser()
            var jsonElement = writer.parse(note!!.content)
            val jsonArray = jsonElement.asJsonArray

            addTitleToLayout(view, note!!.title!!)

            for (el in jsonArray) {

                val blockType = el.asJsonObject["type"].asString
                val dataText = el.asJsonObject["data"].asJsonObject["text"].asString

                val dataType = el.asJsonObject["data"].asJsonObject["heading-styles"]

                //val block = NoteBlock(context!!, blockType, NoteDescription(dataText, dataType?.asString ?: ""))
                val block = NoteBlockFactory.createBlock(context!!, el.asJsonObject)

              //  elements.add(textElement)

                (view.root_note_linear_layout as LinearLayout).addView(block.getView())
            }
        }
        setHasOptionsMenu(true)
        val manager = (activity as MainActivity).supportFragmentManager

        return view
    }

    override fun onDestroy() {
        Log.i("NoteFragment", "is killed")
        super.onDestroy()
    }

    /**
     * Set up title of current note
     * @param view layout container that can adding view
     * @param title Text that showing in title
     */

    private fun addTitleToLayout(view: View, title: String) {
        val jsonObj = JsonObject()//"{\"type\": header, \"data\": {\"text\": $title, \"level\": 1} }")
        jsonObj.addProperty("type", "header")
        val jsonElem = JsonObject()
        jsonElem.addProperty("text", title)
        jsonElem.addProperty("level", 1)
        jsonObj.add("data", jsonElem)
        val blockType = "header"
        val dataText = title

        val dataType = "h1"

        val block = NoteBlockFactory.createBlock(context!!, jsonObj)

        (view.root_note_linear_layout as LinearLayout).addView(block.getView())
    }

    override fun onStop() {
        (activity as MainActivity).showMenu()
        activity!!.invalidateOptionsMenu()
        super.onStop()
    }
}