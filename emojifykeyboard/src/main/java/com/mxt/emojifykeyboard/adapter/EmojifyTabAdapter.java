package com.mxt.emojifykeyboard.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.annimon.stream.Stream;
import com.mxt.emojifykeyboard.fragment.FragmentEmoji;
import com.mxt.emojifykeyboard.model.EmojiKeyboard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.wax911.emojify.Emoji;

/**
 * Created by max on 2017/12/06.
 */

public class EmojifyTabAdapter extends FragmentPagerAdapter {

    private EmojiKeyboard onEmojiClickListener;
    private List<Map.Entry<String, List<Emoji>>> emojiMap;

    public EmojifyTabAdapter(FragmentManager fragmentManager, List<Map.Entry<String, List<Emoji>>> emojiMap) {
        super(fragmentManager);
        this.emojiMap = emojiMap;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        FragmentEmoji fragment = FragmentEmoji.newInstance(emojiMap.get(position).getValue());
        fragment.setEmojiClickListener(onEmojiClickListener);
        return fragment;
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return emojiMap.get(position).getKey();
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return emojiMap.size();
    }

    public void setOnEmojiClickListener(EmojiKeyboard onEmojiClickListener) {
        this.onEmojiClickListener = onEmojiClickListener;
    }
}
