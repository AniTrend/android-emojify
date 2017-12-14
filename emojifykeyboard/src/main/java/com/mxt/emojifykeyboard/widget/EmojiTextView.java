package com.mxt.emojifykeyboard.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by max on 2017/12/06.
 */

public class EmojiTextView extends AppCompatTextView {

    public EmojiTextView(Context context) {
        super(context);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets the text to be displayed and the {@link BufferType}.
     * <p/>
     * When required, TextView will use {@link Spannable.Factory} to create final or
     * intermediate {@link Spannable Spannables}. Likewise it will use
     * {@link Editable.Factory} to create final or intermediate
     * {@link Editable Editables}.
     *
     * @param text text to be displayed
     * @param type a {@link BufferType} which defines whether the text is
     *             stored as a static text, styleable/spannable text, or editable text
     * @attr ref android.R.styleable#TextView_text
     * @attr ref android.R.styleable#TextView_bufferType
     * @see #setText(CharSequence)
     * @see BufferType
     * @see #setSpannableFactory(Spannable.Factory)
     * @see #setEditableFactory(Editable.Factory)
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        if(!TextUtils.isEmpty(text)) {
            Spanned spanned = Html.fromHtml(text.toString());
            super.setText(spanned, type);
        }
        super.setText(text, type);
    }
}
