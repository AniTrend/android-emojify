package io.wax911.emojifysample

import android.os.Bundle
import android.text.Editable
import android.text.Spanned
import android.text.SpannedString
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.wax911.emojify.parser.parseToAliases
import io.wax911.emojify.parser.parseToHtmlDecimal
import io.wax911.emojify.parser.parseToHtmlHexadecimal
import io.wax911.emojify.parser.parseToUnicode
import io.wax911.emojifysample.databinding.ActivityMainBinding
import io.wax911.emojifysample.ext.emojiManager

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.containMain.toEmoji.setOnClickListener(this)
        binding.containMain.toHtml.setOnClickListener(this)
        binding.containMain.toHexHtml.setOnClickListener(this)
        binding.containMain.toShortCode.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val textContent: Editable? = binding.containMain.editText.editableText
        if (textContent == null) {
            Toast.makeText(this, "You must first enter some text", Toast.LENGTH_SHORT).show()
            return
        }
        var convertedText: Spanned? = null
        when (view.id) {
            R.id.toEmoji ->
                // alternatively you could convert your hexHtml to emoji by using Html.fromHtml()
                //e.g. convertedText = Html.fromHtml(EmojiParser.parseToHtmlHexadecimal(textContent.toString()));
                convertedText = SpannedString(emojiManager().parseToUnicode(textContent.toString()))

            R.id.toHtml ->
                // alternatively you could convert your hexHtml to emoji by using Html.fromHtml()
                //e.g. convertedText = Html.fromHtml(EmojiParser.parseToHtmlDecimal(textContent.toString()));
                convertedText =
                    SpannedString(emojiManager().parseToHtmlDecimal(textContent.toString()))

            R.id.toHexHtml ->
                convertedText =
                    SpannedString(emojiManager().parseToHtmlHexadecimal(textContent.toString()))

            R.id.toShortCode ->
                convertedText = SpannedString(emojiManager().parseToAliases(textContent.toString()))
        }
        binding.containMain.editText.setText(convertedText)
    }

    override fun onDestroy() {
        binding.containMain.toEmoji.setOnClickListener(null)
        binding.containMain.toHtml.setOnClickListener(null)
        binding.containMain.toHexHtml.setOnClickListener(null)
        binding.containMain.toShortCode.setOnClickListener(null)
        super.onDestroy()
    }
}
