package com.notesandroid.codex.notesandroid.Utilities

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Shiplayer on 19.11.18.
 */

/**
 * Class for formatting Date using SimpleDateFormat
 */
class DateFormatter {
    companion object {
        @SuppressLint("SimpleDateFormat")
        val year = SimpleDateFormat("yyyy")

        @SuppressLint("SimpleDateFormat")
        val month = SimpleDateFormat("dd MMMM")

        @SuppressLint("SimpleDateFormat")
        val time = SimpleDateFormat("hh:mm")

        /**
         * Method for parsing timestamp to finish string
         * @param timestamp Time between you want show time
         * @param today String that showing text and you should get it from resources
         * @param yesterday Same
         * @return formatted timestamp in already line
         */

        fun parseDate(timestamp: Long, today: String, yesterday: String): String {
            val now = Calendar.getInstance()
            val late = Calendar.getInstance()
            late.timeInMillis = timestamp
            return if (late.get(Calendar.YEAR) == now.get(Calendar.YEAR))
                if (late.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
                    when {
                        late.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) -> today + " " + time.format(late.time)
                        late.get(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH) == 1 -> yesterday + " " + time.format(time)
                        else -> month.format(late.time)
                    }
                } else month.format(late.time)
            else year.format(late.time)
        }
    }
}