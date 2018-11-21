package com.notesandroid.codex.notesandroid.Utilities

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import com.notesandroid.codex.notesandroid.R
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Created by Shiplayer on 08.11.18.
 */

class TextFormatter private constructor() {
    lateinit var text: String
    val builder = SpannableStringBuilder()

    companion object {
        private val mapColor = mutableMapOf<String, Int>()
        fun init(
            context: Context
        ): TextFormatter {
            mapColor["inlineCode"] = getColor(context, R.color.inlineCodeColor)
            return TextFormatter()
        }

        private fun getColor(
            ctx: Context,
            id: Int
        ): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ctx.getColor(id)
            } else {
                ctx.resources.getColor(id)
            }
        }
    }

    fun parse(
        text: String
    ): SpannableString {
        val elem = Jsoup.parse(text)
        val obj = elem.body()
        for (node in obj.childNodes()) {
            builder.append(parseNode(node))
        }
        return SpannableString.valueOf(builder)
    }

    private fun parseNode(
        node: Node
    ): SpannableString {
        val span = SpannableStringBuilder()
        when (node) {
            is TextNode -> {
                span.append(node.text())
            }
            is Element -> {
                if (node.childNodes().size > 1 && node.childNodes()[0] is Element) {
                    span.append(parse(node.html()))
                } else {
                    span.append(parseElement(node))
                }
            }
        }
        return SpannableString.valueOf(span)
    }

    private fun parseElement(
        element: Element
    ): SpannableString {
        val tagName = element.tagName()
        val style = when (tagName) {
            "b" -> StyleSpan(Typeface.BOLD)
            "i" -> StyleSpan(Typeface.ITALIC)
            "span" -> getSpanStyle(element)
            else -> listOf(StyleSpan(Typeface.NORMAL), ForegroundColorSpan(Color.BLUE))
        }
        val result = SpannableStringBuilder()

        if (style is List<Any?>) {
            result.append(element.text())
            for (spanStyle in style) {
                result.setSpan(spanStyle, 0, element.text().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else
            result.append(element.text()).setSpan(style, 0, element.text().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return SpannableString.valueOf(result)
    }

    private fun getSpanStyle(
        element: Element
    ): Any? {
        val list = mutableListOf<Any?>()
        for (className in element.classNames()) {
            list.addAll(
                when (className) {
                    "inline-code" -> listOf(
                        BackgroundColorSpan(mapColor["inlineCode"]!!),
                        ForegroundColorSpan(Color.parseColor("#c44545")),
                        TypefaceSpan("monospace"),
                        RelativeSizeSpan(0.9f)
                    )
                    else -> listOf(null)
                }
            )
        }
        return list
    }

    private fun hasInnerTag(
        element: Element
    ): Boolean {
        return element.childNodes().size != 0
    }
}