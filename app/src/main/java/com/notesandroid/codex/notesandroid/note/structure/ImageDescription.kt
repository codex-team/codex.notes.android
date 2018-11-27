package com.notesandroid.codex.notesandroid.note.structure

import com.google.gson.JsonElement

/**
 * Created by Shiplayer on 27.11.18.
 */

open class ImageDescription(
    type: String,
    image: ImageParams,
    content: String,
    withBorder: Boolean,
    stretched: Boolean,
    withBackground: Boolean
) {
    companion object {
        fun createFrom(originElement: JsonElement): ImageDescription {
            var element = originElement.asJsonObject
            val type = element["type"].asString
            element = element["data"].asJsonObject
            val imageParams = ImageParams(
                element["file"].asJsonObject["url"].asString,
                element["file"].asJsonObject["width"].asInt,
                element["file"].asJsonObject["height"].asInt
            )
            return ImageDescription(
                type,
                imageParams,
                element["content"].asString,
                element["withBorder"].asBoolean,
                element["stretched"].asBoolean,
                element["withBackground"].asBoolean
            )
        }
    }
}

data class ImageParams(
    val url: String,
    val width: Int,
    val height: Int
)