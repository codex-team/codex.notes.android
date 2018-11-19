package com.notesandroid.codex.notesandroid.Utilities

import android.content.Context
import com.notesandroid.codex.notesandroid.DEBUG
import org.jetbrains.anko.toast
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by AksCorp on 02.02.2018.
 */
class MyLog(
  val context: Context
) {

    private fun getSystemDir(): String = "/data/data/com.notesandroid.codex.notesandroid"

    fun log(
      text: String
    ) {
        var text = text
        var logFile = File(getSystemDir() + "/Logs")
        logFile.mkdir()
        logFile = File(getSystemDir() + "/Logs/log.txt")

        val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
        val time = formatter.format(Date()) // 2009-06-30 08:29:36

        text = "[$time] $text"

        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (
              e: IOException
            ) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
        try {
            // BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()

            if (DEBUG)
                context.toast("Exception was logged")
        } catch (
          e: IOException
        ) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
}