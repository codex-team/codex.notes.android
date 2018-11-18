package com.notesandroid.codex.notesandroid.Fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.notesandroid.codex.notesandroid.Activities.MainActivity
import com.notesandroid.codex.notesandroid.Essences.Note
import com.notesandroid.codex.notesandroid.NoteStructure.NoteBlockFactory
import com.notesandroid.codex.notesandroid.R
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar!!.customView = activity!!.layoutInflater.inflate(R.layout.note_actionbar_layout, null)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        /*inflater!!.inflate(R.menu.note_menu, menu)
        if(menu != null){
            val image = menu.findItem(R.id.circleImageBar).actionView as CircleImageView
            image.circleBackgroundColor = Color.GREEN
        }*/
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
            val note = arguments!!["note"] as Note

            val builder = GsonBuilder()
            val gson = builder.create()

            val writer = JsonParser()
            var jsonElement = writer.parse(note.content)
            val jsonArray = jsonElement.asJsonArray

            addTitleToLayout(view, note.title!!)

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

    fun addTitleToLayout(view: View, title: String) {
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