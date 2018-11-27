package com.notesandroid.codex.notesandroid.note.structure

import android.content.Context
import com.google.gson.JsonObject
import com.notesandroid.codex.notesandroid.note.structure.blocks.DelimiterBlock
import com.notesandroid.codex.notesandroid.note.structure.blocks.HeaderBlock
import com.notesandroid.codex.notesandroid.note.structure.blocks.ImageBlock
import com.notesandroid.codex.notesandroid.note.structure.blocks.NoteBlock
import com.notesandroid.codex.notesandroid.note.structure.blocks.ParagraphBlock

/**
 * Note block types
 */
const val PARAGRAPH_BLOCK = "paragraph"
const val HEADER_BLOCK = "header"
const val DELIMITER_BLOCK = "delimiter"
const val IMAGE_BLOCK = "image"


object NoteBlockFactory {
    fun createBlock(
        context: Context,
        obj: JsonObject
    ): NoteBlock {
        if (obj.has("type")) {
            var block: NoteBlock? = null
            when (obj["type"].asString.toLowerCase()) {
                PARAGRAPH_BLOCK -> {
                    block = ParagraphBlock(
                        context,
                        NoteDescription(obj["data"].asJsonObject["text"].asString)
                    )
                }
                HEADER_BLOCK -> {
                    block = HeaderBlock(
                        context, NoteDescription(
                            obj["data"].asJsonObject["text"].asString,
                            "H" + obj["data"].asJsonObject["level"]
                        )
                    )
                }
                DELIMITER_BLOCK -> {
                    block = DelimiterBlock(context)
                }
                IMAGE_BLOCK -> {
                    block = ImageBlock(
                        context,
                        ImageDescription.createFrom(obj)
                    )
                }
            }
            return block!!
        } else throw Throwable("Json object is not corrected. Don't found a member with name \"type\"")
    }
}