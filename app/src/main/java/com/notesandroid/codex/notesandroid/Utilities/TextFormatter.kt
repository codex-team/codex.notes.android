package com.notesandroid.codex.notesandroid.Utilities

import android.text.SpannableString
import android.text.SpannableStringBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Created by Shiplayer on 08.11.18.
 */

class TextFormatter {
    lateinit var text: String
    private var position: Int = 0
    private var deepLevel: Int = 0
    val builder = SpannableStringBuilder()
    private val list = listOf<SpannableString>()

    fun parse(text: String): SpannableString {
        this.text = text
        var elem = Jsoup.parse(text)
        var obj = elem.body()
        for (node in obj.childNodes()) {
            parseNode(node)
        }
        return SpannableString.valueOf(builder)
    }

    private fun parseNode(node: Node): SpannableString {
        val span = SpannableStringBuilder()
        when (node) {
            is TextNode -> {
                span.append(node.text())
            }
            is Element -> {
                if (node.childNodes().size != 0) {
                    span.append(parse(node.html()))
                } else {
                    span.append(parseElement(node))
                }
            }
        }
        return SpannableString.valueOf(span)
    }

    private fun parseElement(element: Element): SpannableString {

        return SpannableString("text")
    }

    private fun hasInnerTag(element: Element): Boolean {
        return element.childNodes().size != 0
    }

    fun getText() {
    }

    private fun getNextTag(element: Element): Tag {

        return Tag("test")
    }

    interface TextSpannable {
        fun getText(): SpannableString
    }

    private class Tag(val text: String) : TextSpannable {
        lateinit var tag: String
        lateinit var data: SpannableString
        var attributes: List<Attribute> = listOf()

        init {

        }

        override fun getText(): SpannableString {
            return data
        }
    }

    private class Text(val text: String) : TextSpannable {
        override fun getText(): SpannableString = SpannableString(text)
    }

    private class Attribute(val text: String) {
        lateinit var name: String
        lateinit var value: String
    }
}