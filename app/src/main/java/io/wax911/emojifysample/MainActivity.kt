package io.wax911.emojifysample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import android.text.Editable
import android.text.Spanned
import android.text.SpannedString
import android.view.View
import android.widget.Toast
import io.wax911.emojify.parser.EmojiParser

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var editText: AppCompatEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.edit_text)

        findViewById<View>(R.id.toEmoji).setOnClickListener(this)
        findViewById<View>(R.id.toHtml).setOnClickListener(this)
        findViewById<View>(R.id.toHexHtml).setOnClickListener(this)
        findViewById<View>(R.id.toShortCode).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val textContent: Editable? = editText?.editableText
        if (textContent == null) {
            Toast.makeText(this, "You must first enter some text", Toast.LENGTH_SHORT).show()
            return
        }
        var convertedText: Spanned? = null
        when (view.id) {
            R.id.toEmoji ->
                // alternatively you could convert your hexHtml to emoji by using Html.fromHtml()
                //e.g. convertedText = Html.fromHtml(EmojiParser.parseToHtmlHexadecimal(textContent.toString()));
                convertedText = SpannedString(EmojiParser.parseToUnicode(textContent.toString()))
            R.id.toHtml ->
                // alternatively you could convert your hexHtml to emoji by using Html.fromHtml()
                //e.g. convertedText = Html.fromHtml(EmojiParser.parseToHtmlDecimal(textContent.toString()));
                convertedText = SpannedString(EmojiParser.parseToHtmlDecimal(textContent.toString()))
            R.id.toHexHtml ->
                convertedText = SpannedString(EmojiParser.parseToHtmlHexadecimal(textContent.toString()))
            R.id.toShortCode ->
                convertedText = SpannedString(EmojiParser.parseToAliases(textContent.toString()))
        }
        editText?.setText(convertedText)
    }
}
