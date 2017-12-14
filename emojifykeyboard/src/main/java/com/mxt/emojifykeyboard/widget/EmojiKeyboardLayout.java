package com.mxt.emojifykeyboard.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.mxt.emojifykeyboard.R;
import com.mxt.emojifykeyboard.model.EmojiKeyboard;

/**
 * Created by max on 2017/12/07.
 */

public class EmojiKeyboardLayout extends LinearLayout {

    public EmojiKeyboardLayout(Context context) {
        super(context);
        onInit();
    }

    public EmojiKeyboardLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onInit();
    }

    public EmojiKeyboardLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EmojiKeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onInit();
    }

    private void onInit() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null)
            inflater.inflate(R.layout.emoji_keyboard_layout, this, true);
        findViewById(R.id.emoji_keyboard).setVisibility(LinearLayout.VISIBLE);
    }

    public void prepareKeyboard (FragmentActivity activity, EmojiTextView input) {
        new EmojiKeyboard(activity, input);
    }
}
