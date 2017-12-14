package com.mxt.emojifykeyboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mxt.emojifykeyboard.R;
import com.mxt.emojifykeyboard.widget.EmojiTextView;

import java.util.List;

import io.wax911.emojify.Emoji;

/**
 * Created by max on 2017/12/06.
 */

public class EmojifyAdapter extends ArrayAdapter<Emoji> {

    public EmojifyAdapter(Context context, Emoji[] data) {
        super(context, R.layout.emoji_item, data);
    }

    public EmojifyAdapter(Context context, List<Emoji> data) {
        super(context, R.layout.emoji_item, data);
    }

    @Nullable
    @Override
    public Emoji getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = View.inflate(getContext(), R.layout.emoji_item, null);
            view.setTag(new ViewHolder(view));
        }

        Emoji emoji;
        if ((emoji = getItem(position)) != null) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.icon.setText(emoji.getEmoji());
        }

        return view;
    }

    protected class ViewHolder {
        EmojiTextView icon;

        ViewHolder(View view) {
            this.icon = view.findViewById(R.id.emoji_icon);
        }
    }
}
