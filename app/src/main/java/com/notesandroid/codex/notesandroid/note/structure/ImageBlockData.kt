package com.notesandroid.codex.notesandroid.note.structure

import com.google.gson.JsonElement

/**
 * Created by Shiplayer on 27.11.18.
 */

data class ImageBlockData(
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
        ): ImageBlockData {
            var element = originElement.asJsonObject
            val type = element["type"].asString
            element = element["data"].asJsonObject
            val imageParams = if (element.has("file")) {
                ImageParams(
                    element["file"].asJsonObject["url"].asString,
                    element["file"].asJsonObject["width"].asInt,
                    element["file"].asJsonObject["height"].asInt
                )
            } else {
                ImageParams(
                    element["url"].asString
                )
            }
            return ImageBlockData(
                type,
                imageParams,
                element["caption"].asString,
                element["withBorder"].asBoolean,
                element["stretched"].asBoolean,
                element["withBackground"].asBoolean
            )
        }
    }

    override fun toString(): String {
        return "ImageBlockData(type='$type', image=$image, content='$content', withBorder=$withBorder, stretched=$stretched, withBackground=$withBackground)"
    }


}

data class ImageParams(
    val url: String,
    val width: Int? = null,
    val height: Int? = null
) {
    override fun toString(): String {
        return "ImageParams(url='$url', width=$width, height=$height)"
    }
}