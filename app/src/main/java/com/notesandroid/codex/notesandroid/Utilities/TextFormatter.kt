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

/**
 * Class that don't have public constructor. First need invoke init with context for initialize map of colors for text style.
 *
 */

class TextFormatter private constructor() {
    lateinit var text: String
    val builder = SpannableStringBuilder()

    companion object {
        private val mapColor = mutableMapOf<String, Int>()

        /**
         * Method for initialize map of color
         * @param context - [Context] used for getting color from resources
         * @return new instance of [TextFormatter]
         */
        fun init(
            context: Context
        ): TextFormatter {
            mapColor["inlineCode"] = getColor(context, R.color.inlineCodeColor)
            return TextFormatter()
        }

        /**
         * Method for getting color from resources
         * @param ctx - [Context] for getting color from resources
         * @param id - Id of color from resources
         * @return color as representation in int
         */
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

    /**
     * Parse html text to text with applied styles that understanding TextView
     * @param text - Text have html code
     * @return
     */

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

    /**
     *
     * @param node - that may contain or TextNode or Element
     * @return spanned string that applied style
     */

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

    /**
     * @param element - Element that contains tag like <b>, <i> and etc.
     * @return spanned string that applied style for current tag
     */

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

    /**
     *
     * @param element - Complex element that contain class
     * @return spanned string that applied style for current class
     */

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
}