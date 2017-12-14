package com.mxt.emojifykeyboard.model;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.mxt.emojifykeyboard.R;
import com.mxt.emojifykeyboard.adapter.EmojifyTabAdapter;
import com.mxt.emojifykeyboard.event.OnEmojiClickListener;
import com.mxt.emojifykeyboard.widget.EmojiTextView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.List;
import java.util.Map;

import io.wax911.emojify.Emoji;
import io.wax911.emojify.EmojiUtils;

/**
 * Created by max on 2017/12/07.
 */

public class EmojiKeyboard implements OnEmojiClickListener {

    private LinearLayout mEmojiKeyboardLayout;
    private FragmentActivity activity;
    private EmojiTextView mInput;
    private ImageView mBackspace;

    public EmojiKeyboard(FragmentActivity activity, EmojiTextView input) {
        this.mInput = input;
        this.activity = activity;
        this.mEmojiKeyboardLayout = activity.findViewById(R.id.emoji_keyboard);
        this.initEmojiKeyboardViewPager();
    }

    private void initEmojiKeyboardViewPager() {
        Map<String, List<Emoji>> map = Stream.of(EmojiUtils.getAllEmojis())
                .collect(Collectors.groupingBy(Emoji::getCategory));
        EmojifyTabAdapter tabAdapter = new EmojifyTabAdapter(activity.getSupportFragmentManager(), Stream.of(map).toList());
        tabAdapter.setOnEmojiClickListener(this);
        ViewPager viewPager = activity.findViewById(R.id.emoji_viewpager);
        viewPager.setAdapter(tabAdapter);
        SmartTabLayout viewPagerTab = activity.findViewById(R.id.emoji_tabs);
        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    public void onEmojiClicked(Emoji emoji) {
        int start = this.mInput.getSelectionStart();
        int end = this.mInput.getSelectionEnd();

        if (start < 0) {
            this.mInput.append(emoji.getEmoji());
        } else {
            this.mInput.getEditableText().replace(Math.min(start, end), Math.max(start, end), emoji.getEmoji(), 0, emoji.getEmoji().length());
        }
    }
}
