package codex.notesandroid.utilities

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import codex.notesandroid.R
import codex.notesandroid.spans.CodeXMarker
import codex.notesandroid.spans.CodeXURLSpan
import codex.notesandroid.spans.InlineStyleSpan
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
            mapColor["inlineCode"] = getColor(context, R.color.inlineCodeColorBackground)
            mapColor["inlineCodeForeground"] = getColor(context, R.color.inlineCodeColorForeground)
            mapColor["linkColor"] = getColor(context, R.color.linkColor)
            mapColor["markerColor"] = getColor(context, R.color.markerColor)
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
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        val elem = Jsoup.parse(text)
        val obj = elem.body()
        for (node in obj.childNodes()) {
            builder.append(parseNode(node))
        }
        val arraySpan = builder.getSpans(0, builder.length, StyleSpan::class.java)
        /*Log.i("TextFormatter", "Text $builder")
        for (span in arraySpan)
            Log.i("TextFormatter", getIntToSpanStyle(span.style))*/
        return builder
    }

    /**
     * Return String representation for span style id
     */
    private fun getIntToSpanStyle(
        id: Int
    ): String {
        return when (id) {
            Typeface.BOLD ->
                "BOLD"
            Typeface.ITALIC ->
                "ITALIC"
            else ->
                "UNKNOWN"
        }
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
                if (node.childNodes().size > 1 && (node.childNodes()[0] is Element || node.childNodes()[1] is Element)) {
                    span.append(parse(node.html()))
                    span.setSpan(getSpanStyleByTag(node), 0, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
        val style = getSpanStyleByTag(element)
        val result = parse(element.html())

        if (style is List<Any?>) {
            // result.append(element.text())
            for (spanStyle in style) {
                result.setSpan(spanStyle, 0, element.text().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else
            result.setSpan(style, 0, element.text().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return SpannableString.valueOf(result)
    }

    /**
     * @param element - The tag for which need to get the style
     * @return SpanStyle or list of spanStyles which need to get the style, but represented as Any?
     */
    fun getSpanStyleByTag(
        element: Element
    ): Any? {
        val tagName = element.tagName()
        return when (tagName) {
            "b" -> StyleSpan(Typeface.BOLD)
            "i" -> StyleSpan(Typeface.ITALIC)
            "a" -> CodeXURLSpan(element.attr("href"), mapColor["linkColor"]!!)//URLSpan(element.attr("href"))
            "span" -> getSpanStyle(element)
            else -> listOf(StyleSpan(Typeface.NORMAL), ForegroundColorSpan(Color.BLUE))
        }
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
                        InlineStyleSpan(
                            mapColor["inlineCode"]!!,
                            mapColor["inlineCodeForeground"]!!
                        )
                    )
                    "cdx-marker" -> listOf(
                        CodeXMarker(mapColor["markerColor"]!!)
                    )
                    else -> listOf(null)
                }
            )
        }
        return list
    }
}