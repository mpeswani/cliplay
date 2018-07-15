/*
 * Copyright (C) 2015 Matthew Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clipay.texteditor

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.style.*
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import java.util.*

open class KnifeText : EditText, TextWatcher {

    private var bulletColor = 0
    private var bulletRadius = 0
    private var bulletGapWidth = 0
    private var historyEnable = true
    private var historySize = 100
    private var linkColor = 0
    private var linkUnderline = true
    private var quoteColor = 0
    private var quoteStripeWidth = 0
    private var quoteGapWidth = 0

    private val historyList = LinkedList<Editable>()
    private var historyWorking = false
    private var historyCursor = 0

    private var inputBefore: SpannableStringBuilder? = null
    private var inputLast: Editable? = null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.KnifeText)
        bulletColor = array.getColor(R.styleable.KnifeText_bulletColor, 0)
        bulletRadius = array.getDimensionPixelSize(R.styleable.KnifeText_bulletRadius, 0)
        bulletGapWidth = array.getDimensionPixelSize(R.styleable.KnifeText_bulletGapWidth, 0)
        historyEnable = array.getBoolean(R.styleable.KnifeText_historyEnable, true)
        historySize = array.getInt(R.styleable.KnifeText_historySize, 100)
        linkColor = array.getColor(R.styleable.KnifeText_linkColor, 0)
        linkUnderline = array.getBoolean(R.styleable.KnifeText_linkUnderline, true)
        quoteColor = array.getColor(R.styleable.KnifeText_quoteColor, 0)
        quoteStripeWidth = array.getDimensionPixelSize(R.styleable.KnifeText_quoteStripeWidth, 0)
        quoteGapWidth = array.getDimensionPixelSize(R.styleable.KnifeText_quoteCapWidth, 0)
        array.recycle()

        if (historyEnable && historySize <= 0) {
            throw IllegalArgumentException("historySize must > 0")
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        addTextChangedListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeTextChangedListener(this)
    }

    // StyleSpan ===================================================================================

    fun bold(valid: Boolean) {
        if (valid) {
            styleValid(Typeface.BOLD, selectionStart, selectionEnd)
        } else {
            styleInvalid(Typeface.BOLD, selectionStart, selectionEnd)
        }
    }

    fun italic(valid: Boolean) {
        if (valid) {
            styleValid(Typeface.ITALIC, selectionStart, selectionEnd)
        } else {
            styleInvalid(Typeface.ITALIC, selectionStart, selectionEnd)
        }
    }

    private fun styleValid(style: Int, start: Int, end: Int) {
        when (style) {
            Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, Typeface.BOLD_ITALIC -> {
            }
            else -> return
        }

        if (start >= end) {
            return
        }

        editableText.setSpan(StyleSpan(style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun styleInvalid(style: Int, start: Int, end: Int) {
        when (style) {
            Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, Typeface.BOLD_ITALIC -> {
            }
            else -> return
        }

        if (start >= end) {
            return
        }

        val spans = editableText.getSpans<StyleSpan>(start, end, StyleSpan::class.java)
        val list = ArrayList<KnifePart>()

        for (span in spans) {
            if (span.style == style) {
                list.add(KnifePart(editableText.getSpanStart(span), editableText.getSpanEnd(span)))
                editableText.removeSpan(span)
            }
        }

        for (part in list) {
            if (part.isValid) {
                if (part.start < start) {
                    styleValid(style, part.start, start)
                }

                if (part.end > end) {
                    styleValid(style, end, part.end)
                }
            }
        }
    }

    private fun containStyle(style: Int, start: Int, end: Int): Boolean {
        when (style) {
            Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, Typeface.BOLD_ITALIC -> {
            }
            else -> return false
        }

        if (start > end) {
            return false
        }

        if (start == end) {
            if (start - 1 < 0 || start + 1 > editableText.length) {
                return false
            } else {
                val before = editableText.getSpans<StyleSpan>(start - 1, start, StyleSpan::class.java)
                val after = editableText.getSpans<StyleSpan>(start, start + 1, StyleSpan::class.java)
                return before.isNotEmpty() && after.isNotEmpty() && before[0].style == style && after[0].style == style
            }
        } else {
            val builder = StringBuilder()

            // Make sure no duplicate characters be added
            for (i in start until end) {
                val spans = editableText.getSpans<StyleSpan>(i, i + 1, StyleSpan::class.java)
                for (span in spans) {
                    if (span.style == style) {
                        builder.append(editableText.subSequence(i, i + 1).toString())
                        break
                    }
                }
            }

            return editableText.subSequence(start, end).toString() == builder.toString()
        }
    }

    // UnderlineSpan ===============================================================================

    fun underline(valid: Boolean) {
        if (valid) {
            underlineValid(selectionStart, selectionEnd)
        } else {
            underlineInvalid(selectionStart, selectionEnd)
        }
    }

    private fun underlineValid(start: Int, end: Int) {
        if (start >= end) {
            return
        }

        editableText.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun underlineInvalid(start: Int, end: Int) {
        if (start >= end) {
            return
        }

        val spans = editableText.getSpans<UnderlineSpan>(start, end, UnderlineSpan::class.java)
        val list = ArrayList<KnifePart>()

        for (span in spans) {
            list.add(KnifePart(editableText.getSpanStart(span), editableText.getSpanEnd(span)))
            editableText.removeSpan(span)
        }

        for (part in list) {
            if (part.isValid) {
                if (part.start < start) {
                    underlineValid(part.start, start)
                }

                if (part.end > end) {
                    underlineValid(end, part.end)
                }
            }
        }
    }

    private fun containUnderline(start: Int, end: Int): Boolean {
        if (start > end) {
            return false
        }

        if (start == end) {
            if (start - 1 < 0 || start + 1 > editableText.length) {
                return false
            } else {
                val before = editableText.getSpans<UnderlineSpan>(start - 1, start, UnderlineSpan::class.java)
                val after = editableText.getSpans<UnderlineSpan>(start, start + 1, UnderlineSpan::class.java)
                return before.isNotEmpty() && after.isNotEmpty()
            }
        } else {
            val builder = StringBuilder()

            for (i in start until end) {
                if (editableText.getSpans<UnderlineSpan>(i, i + 1, UnderlineSpan::class.java).isNotEmpty()) {
                    builder.append(editableText.subSequence(i, i + 1).toString())
                }
            }

            return editableText.subSequence(start, end).toString() == builder.toString()
        }
    }

    // StrikethroughSpan ===========================================================================

    fun strikethrough(valid: Boolean) {
        if (valid) {
            strikethroughValid(selectionStart, selectionEnd)
        } else {
            strikethroughInvalid(selectionStart, selectionEnd)
        }
    }

    private fun strikethroughValid(start: Int, end: Int) {
        if (start >= end) {
            return
        }

        editableText.setSpan(StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun strikethroughInvalid(start: Int, end: Int) {
        if (start >= end) {
            return
        }

        val spans = editableText.getSpans<StrikethroughSpan>(start, end, StrikethroughSpan::class.java)
        val list = ArrayList<KnifePart>()

        for (span in spans) {
            list.add(KnifePart(editableText.getSpanStart(span), editableText.getSpanEnd(span)))
            editableText.removeSpan(span)
        }

        for (part in list) {
            if (part.isValid) {
                if (part.start < start) {
                    strikethroughValid(part.start, start)
                }

                if (part.end > end) {
                    strikethroughValid(end, part.end)
                }
            }
        }
    }

    private fun containStrikethrough(start: Int, end: Int): Boolean {
        if (start > end) {
            return false
        }

        if (start == end) {
            if (start - 1 < 0 || start + 1 > editableText.length) {
                return false
            } else {
                val before = editableText.getSpans<StrikethroughSpan>(start - 1, start, StrikethroughSpan::class.java)
                val after = editableText.getSpans<StrikethroughSpan>(start, start + 1, StrikethroughSpan::class.java)
                return before.isNotEmpty() && after.isNotEmpty()
            }
        } else {
            val builder = StringBuilder()

            for (i in start until end) {
                if (editableText.getSpans<StrikethroughSpan>(i, i + 1, StrikethroughSpan::class.java).isNotEmpty()) {
                    builder.append(editableText.subSequence(i, i + 1).toString())
                }
            }

            return editableText.subSequence(start, end).toString() == builder.toString()
        }
    }

    // BulletSpan ==================================================================================

    fun bullet(valid: Boolean) {
        if (valid) {
            bulletValid()
        } else {
            bulletInvalid()
        }
    }

    private fun bulletValid() {
        val lines = TextUtils.split(editableText.toString(), "\n")

        for (i in lines.indices) {
            if (containBullet(i)) {
                continue
            }

            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1 // \n
            }

            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) {
                continue
            }

            // Find selection area inside
            var bulletStart = 0
            var bulletEnd = 0
            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                bulletStart = lineStart
                bulletEnd = lineEnd
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                bulletStart = lineStart
                bulletEnd = lineEnd
            }

            if (bulletStart < bulletEnd) {
                editableText.setSpan(KnifeBulletSpan(bulletColor, bulletRadius, bulletGapWidth), bulletStart, bulletEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun bulletInvalid() {
        val lines = TextUtils.split(editableText.toString(), "\n")

        for (i in lines.indices) {
            if (!containBullet(i)) {
                continue
            }

            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1
            }

            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) {
                continue
            }

            var bulletStart = 0
            var bulletEnd = 0
            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                bulletStart = lineStart
                bulletEnd = lineEnd
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                bulletStart = lineStart
                bulletEnd = lineEnd
            }

            if (bulletStart < bulletEnd) {
                val spans = editableText.getSpans<BulletSpan>(bulletStart, bulletEnd, BulletSpan::class.java)
                for (span in spans) {
                    editableText.removeSpan(span)
                }
            }
        }
    }

    private fun containBullet(): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")
        val list = ArrayList<Int>()

        for (i in lines.indices) {
            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1
            }

            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) {
                continue
            }

            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                list.add(i)
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                list.add(i)
            }
        }

        for (i in list) {
            if (!containBullet(i)) {
                return false
            }
        }

        return true
    }

    private fun containBullet(index: Int): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")
        if (index < 0 || index >= lines.size) {
            return false
        }

        var start = 0
        for (i in 0 until index) {
            start += lines[i].length + 1
        }

        val end = start + lines[index].length
        if (start >= end) {
            return false
        }

        val spans = editableText.getSpans<BulletSpan>(start, end, BulletSpan::class.java)
        return spans.isNotEmpty()
    }

    // QuoteSpan ===================================================================================

    fun quote(valid: Boolean) {
        if (valid) {
            quoteValid()
        } else {
            quoteInvalid()
        }
    }

    private fun quoteValid() {
        val lines = TextUtils.split(editableText.toString(), "\n")

        for (i in lines.indices) {
            if (containQuote(i)) {
                continue
            }

            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1 // \n
            }

            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) {
                continue
            }

            var quoteStart = 0
            var quoteEnd = 0
            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                quoteStart = lineStart
                quoteEnd = lineEnd
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                quoteStart = lineStart
                quoteEnd = lineEnd
            }

            if (quoteStart < quoteEnd) {
                editableText.setSpan(KnifeQuoteSpan(quoteColor, quoteStripeWidth, quoteGapWidth), quoteStart, quoteEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun quoteInvalid() {
        val lines = TextUtils.split(editableText.toString(), "\n")

        for (i in lines.indices) {
            if (!containQuote(i)) {
                continue
            }

            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1
            }

            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) {
                continue
            }

            var quoteStart = 0
            var quoteEnd = 0
            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                quoteStart = lineStart
                quoteEnd = lineEnd
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                quoteStart = lineStart
                quoteEnd = lineEnd
            }

            if (quoteStart < quoteEnd) {
                val spans = editableText.getSpans<QuoteSpan>(quoteStart, quoteEnd, QuoteSpan::class.java)
                for (span in spans) {
                    editableText.removeSpan(span)
                }
            }
        }
    }

    private fun containQuote(): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")
        val list = ArrayList<Int>()

        for (i in lines.indices) {
            var lineStart = 0
            for (j in 0 until i) {
                lineStart += lines[j].length + 1
            }

            val lineEnd = lineStart + lines[i].length
            if (lineStart >= lineEnd) {
                continue
            }

            if (lineStart <= selectionStart && selectionEnd <= lineEnd) {
                list.add(i)
            } else if (selectionStart <= lineStart && lineEnd <= selectionEnd) {
                list.add(i)
            }
        }

        for (i in list) {
            if (!containQuote(i)) {
                return false
            }
        }

        return true
    }

    private fun containQuote(index: Int): Boolean {
        val lines = TextUtils.split(editableText.toString(), "\n")
        if (index < 0 || index >= lines.size) {
            return false
        }

        var start = 0
        for (i in 0 until index) {
            start += lines[i].length + 1
        }

        val end = start + lines[index].length
        if (start >= end) {
            return false
        }

        val spans = editableText.getSpans<QuoteSpan>(start, end, QuoteSpan::class.java)
        return spans.isNotEmpty()
    }

    // When KnifeText lose focus, use this method
    @JvmOverloads
    fun link(link: String?, start: Int = selectionStart, end: Int = selectionEnd) {
        if (link != null && !TextUtils.isEmpty(link.trim { it <= ' ' })) {
            linkValid(link, start, end)
        } else {
            linkInvalid(start, end)
        }
    }

    private fun linkValid(link: String, start: Int, end: Int) {
        if (start >= end) {
            return
        }

        linkInvalid(start, end)
        editableText.setSpan(KnifeURLSpan(link, linkColor, linkUnderline), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    // Remove all span in selection, not like the boldInvalid()
    private fun linkInvalid(start: Int, end: Int) {
        if (start >= end) {
            return
        }

        val spans = editableText.getSpans<URLSpan>(start, end, URLSpan::class.java)
        for (span in spans) {
            editableText.removeSpan(span)
        }
    }

    private fun containLink(start: Int, end: Int): Boolean {
        if (start > end) {
            return false
        }

        if (start == end) {
            if (start - 1 < 0 || start + 1 > editableText.length) {
                return false
            } else {
                val before = editableText.getSpans<URLSpan>(start - 1, start, URLSpan::class.java)
                val after = editableText.getSpans<URLSpan>(start, start + 1, URLSpan::class.java)
                return before.isNotEmpty() && after.isNotEmpty()
            }
        } else {
            val builder = StringBuilder()

            for (i in start until end) {
                if (editableText.getSpans<URLSpan>(i, i + 1, URLSpan::class.java).isNotEmpty()) {
                    builder.append(editableText.subSequence(i, i + 1).toString())
                }
            }

            return editableText.subSequence(start, end).toString() == builder.toString()
        }
    }

    // Redo/Undo ===================================================================================

    override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
        if (!historyEnable || historyWorking) {
            return
        }

        inputBefore = SpannableStringBuilder(text)
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        // DO NOTHING HERE
    }

    override fun afterTextChanged(text: Editable?) {
        if (!historyEnable || historyWorking) {
            return
        }

        inputLast = SpannableStringBuilder(text)
        if (text != null && text.toString() == inputBefore!!.toString()) {
            return
        }

        if (historyList.size >= historySize) {
            historyList.removeAt(0)
        }

        inputBefore?.let { historyList.add(it) }
        historyCursor = historyList.size
    }

    fun redo() {
        if (!redoValid()) {
            return
        }

        historyWorking = true

        if (historyCursor >= historyList.size - 1) {
            historyCursor = historyList.size
            text = inputLast
        } else {
            historyCursor++
            text = historyList[historyCursor]
        }

        setSelection(editableText.length)
        historyWorking = false
    }

    fun undo() {
        if (!undoValid()) {
            return
        }

        historyWorking = true

        historyCursor--
        text = historyList[historyCursor]
        setSelection(editableText.length)

        historyWorking = false
    }

    private fun redoValid(): Boolean {
        return if (!historyEnable || historySize <= 0 || historyList.size <= 0 || historyWorking) {
            false
        } else historyCursor < historyList.size - 1 || historyCursor >= historyList.size - 1 && inputLast != null

    }

    private fun undoValid(): Boolean {
        if (!historyEnable || historySize <= 0 || historyWorking) {
            return false
        }

        return !(historyList.size <= 0 || historyCursor <= 0)

    }

    fun clearHistory() {
        historyList.clear()
    }

    // Helper ======================================================================================

    operator fun contains(format: Int): Boolean {
        when (format) {
            FORMAT_BOLD -> return containStyle(Typeface.BOLD, selectionStart, selectionEnd)
            FORMAT_ITALIC -> return containStyle(Typeface.ITALIC, selectionStart, selectionEnd)
            FORMAT_UNDERLINED -> return containUnderline(selectionStart, selectionEnd)
            FORMAT_STRIKETHROUGH -> return containStrikethrough(selectionStart, selectionEnd)
            FORMAT_BULLET -> return containBullet()
            FORMAT_QUOTE -> return containQuote()
            FORMAT_LINK -> return containLink(selectionStart, selectionEnd)
            else -> return false
        }
    }

    fun clearFormats() {
        setText(editableText.toString())
        setSelection(editableText.length)
    }

    fun hideSoftInput() {
        clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun showSoftInput() {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun fromHtml(source: String) {
        val builder = SpannableStringBuilder()
        builder.append(KnifeParser.fromHtml(source))
        switchToKnifeStyle(builder, 0, builder.length)
        text = builder
    }

    fun toHtml(): String {
        return KnifeParser.toHtml(editableText)
    }

    private fun switchToKnifeStyle(editable: Editable, start: Int, end: Int) {
        val bulletSpans = editable.getSpans<BulletSpan>(start, end, BulletSpan::class.java)
        for (span in bulletSpans) {
            val spanStart = editable.getSpanStart(span)
            var spanEnd = editable.getSpanEnd(span)
            spanEnd = if (0 < spanEnd && spanEnd < editable.length && editable[spanEnd] == '\n') spanEnd - 1 else spanEnd
            editable.removeSpan(span)
            editable.setSpan(KnifeBulletSpan(bulletColor, bulletRadius, bulletGapWidth), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val quoteSpans = editable.getSpans<QuoteSpan>(start, end, QuoteSpan::class.java)
        for (span in quoteSpans) {
            val spanStart = editable.getSpanStart(span)
            var spanEnd = editable.getSpanEnd(span)
            spanEnd = if (0 < spanEnd && spanEnd < editable.length && editable[spanEnd] == '\n') spanEnd - 1 else spanEnd
            editable.removeSpan(span)
            editable.setSpan(KnifeQuoteSpan(quoteColor, quoteStripeWidth, quoteGapWidth), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val urlSpans = editable.getSpans<URLSpan>(start, end, URLSpan::class.java)
        for (span in urlSpans) {
            val spanStart = editable.getSpanStart(span)
            val spanEnd = editable.getSpanEnd(span)
            editable.removeSpan(span)
            editable.setSpan(KnifeURLSpan(span.url, linkColor, linkUnderline), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    companion object {
        const val FORMAT_BOLD = 0x01
        const val FORMAT_ITALIC = 0x02
        const val FORMAT_UNDERLINED = 0x03
        const val FORMAT_STRIKETHROUGH = 0x04
        const val FORMAT_BULLET = 0x05
        const val FORMAT_QUOTE = 0x06
        const val FORMAT_LINK = 0x07
    }
}// URLSpan =====================================================================================
