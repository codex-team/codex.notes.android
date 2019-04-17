package codex.notesandroid.utilities

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import java.io.FileOutputStream
import java.net.URL

/**
 * Created by AksCorp on 04.02.2018.
 */
class Utilities {

    companion object {
        fun isInternetConnected(
          context: Context
        ): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork?.isConnected ?: false // no info object implies no connectivity
        }

        fun saveImageByURL(
          photoURL: String,
          savePath: String
        ) {
            // val filePath = "$storagePath/${IMAGES_DIRECTORY}/${UserData.FIELDS.PROFILE_ICON}.$imageExtension"
            val url = URL(photoURL)
            val input = url.openStream()
            try {
                val output = FileOutputStream(savePath)
                try {
                    val buffer = ByteArray(4*1024)
                    var bytesRead = 1
                    while (bytesRead > 0) {
                        bytesRead = input.read(buffer, 0, buffer.size)
                        if (bytesRead <= 0)
                            break
                        output.write(buffer, 0, bytesRead)
                    }
                } finally {
                    output.close()
                }
            } finally {
                input.close()
            }
        }

        fun getDrawableByUrl(
          photoURL: String
        ): Drawable {
            return BitmapDrawable.createFromStream(URL(photoURL).openStream(), "logo")
        }
    }
}