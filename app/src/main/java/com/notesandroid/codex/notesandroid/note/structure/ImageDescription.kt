package com.notesandroid.codex.notesandroid.note.structure

import com.google.gson.JsonElement

/**
 * Created by Shiplayer on 27.11.18.
 */

data class ImageDescription(
    val type: String,
    val image: ImageParams,
    val content: String,
    val withBorder: Boolean,
    val stretched: Boolean,
    val withBackground: Boolean
) {
    companion object {
        fun createFrom(
            originElement: JsonElement
        ): ImageDescription {
            var element = originElement.asJsonObject
            val type = element["type"].asString
            element = element["data"].asJsonObject
            val imageParams = if (element.has("file")) {
                ImageParams(
                    element["url"].asString,
                    element["file"].asJsonObject["width"].asInt,
                    element["file"].asJsonObject["height"].asInt
                )
            } else {
                ImageParams(
                    element["url"].asString
                )
            }
            return ImageDescription(
                type,
                imageParams,
                element["caption"].asString,
                element["withBorder"].asBoolean,
                element["stretched"].asBoolean,
                element["withBackground"].asBoolean
            )
        }
    }
}

data class ImageParams(
    val url: String,
    val width: Int? = null,
    val height: Int? = null
)