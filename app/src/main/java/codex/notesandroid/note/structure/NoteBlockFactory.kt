package codex.notesandroid.note.structure

import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.gson.JsonObject
import codex.notesandroid.note.structure.blocks.*

/**
 * Note block types
 */
const val PARAGRAPH_BLOCK = "paragraph"
const val HEADER_BLOCK = "header"
const val DELIMITER_BLOCK = "delimiter"
const val IMAGE_BLOCK = "image"
const val LIST_BLOCK = "list"

object NoteBlockFactory {
    fun createBlock(
        context: Context,
        obj: JsonObject
    ): NoteBlock {
        if (obj.has("type")) {
            var block: NoteBlock? = null
            block = when (obj["type"].asString.toLowerCase()) {
                PARAGRAPH_BLOCK -> {
                    ParagraphBlock(
                        context,
                        NoteDescription(obj["data"].asJsonObject["text"].asString)
                    )
                }
                HEADER_BLOCK -> {
                    HeaderBlock(
                        context, NoteDescription(
                            obj["data"].asJsonObject["text"].asString,
                            "H" + obj["data"].asJsonObject["level"]
                        )
                    )
                }
                DELIMITER_BLOCK -> {
                    DelimiterBlock(context)
                }
                IMAGE_BLOCK -> {
                    ImageBlock(
                        context,
                        ImageBlockData.createFrom(obj)
                    )
                }
                LIST_BLOCK -> {
                    ListBlock(
                        context,
                        obj["data"]
                    )
                }
                else -> {
                    object : NoteBlock(context) {
                        override fun getView(): View {
                            val textView = TextView(context)
                            textView.setText("not supported")
                            return textView
                        }
                    }
                }
            }
            return block
        } else throw Throwable("Json object is not corrected. Don't found a member with name \"type\"")
    }
}