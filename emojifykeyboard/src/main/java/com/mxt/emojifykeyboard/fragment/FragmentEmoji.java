package com.mxt.emojifykeyboard.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mxt.emojifykeyboard.R;
import com.mxt.emojifykeyboard.adapter.EmojifyAdapter;
import com.mxt.emojifykeyboard.event.OnEmojiClickListener;

import java.util.ArrayList;
import java.util.List;

import io.wax911.emojify.Emoji;

/**
 * Created by max on 2017/12/06.
 */

public class FragmentEmoji extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "FragmentEmoji";

    private OnEmojiClickListener emojiClickListener;
    private List<Emoji> emojiList;

    public static FragmentEmoji newInstance(List<Emoji> emojiCategory) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(TAG, (ArrayList<? extends Parcelable>) emojiCategory);
        FragmentEmoji fragment = new FragmentEmoji();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            emojiList = getArguments().getParcelableArrayList(TAG);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emoji, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GridView gridView = view.findViewById(R.id.emoji_grid_view);
        gridView.setAdapter(new EmojifyAdapter(view.getContext(), emojiList));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(emojiClickListener != null) {
            Emoji emoji = (Emoji) parent.getItemAtPosition(position);
            emojiClickListener.onEmojiClicked(emoji);
        }
    }

    public void setEmojiClickListener(OnEmojiClickListener emojiClickListener) {
        this.emojiClickListener = emojiClickListener;
    }
}
