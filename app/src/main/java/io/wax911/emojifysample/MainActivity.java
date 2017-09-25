package io.wax911.emojifysample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Toast;

import io.wax911.emojify.Emoji;
import io.wax911.emojify.EmojiUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit_text);

        findViewById(R.id.toEmoji).setOnClickListener(this);
        findViewById(R.id.toHtml).setOnClickListener(this);
        findViewById(R.id.toHexHtml).setOnClickListener(this);
        findViewById(R.id.toShortCode).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Editable textContent;
        if((textContent = editText.getEditableText()) == null) {
            Toast.makeText(this, "You must first enter some text", Toast.LENGTH_SHORT).show();
            return;
        }
        String convertedText = null;
        switch (view.getId()) {
            case R.id.toEmoji:
                convertedText = EmojiUtils.emojify(textContent.toString());
                break;
            case R.id.toHtml:
                convertedText = EmojiUtils.htmlify(textContent.toString());
                break;
            case R.id.toHexHtml:
                convertedText = EmojiUtils.hexHtmlify(textContent.toString());
                break;
            case R.id.toShortCode:
                convertedText = EmojiUtils.shortCodify(textContent.toString());
                break;
        }
        editText.setText(convertedText);
    }
}
