package com.mobileframe.widegt;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.textclassifier.TextClassifier;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by tgl on 2016/8/22.
 * 自定义文字滚动TextView
 */
public class ScrollTextView extends TextView {
    private boolean isFocused = false;

    public ScrollTextView(Context context) {
        super(context);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setMarqueeRepeatLimit(-1);
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isFocused = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        isFocused = false;
        super.onDetachedFromWindow();
    }
}

