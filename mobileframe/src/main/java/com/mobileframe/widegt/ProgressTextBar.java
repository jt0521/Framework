package com.mobileframe.widegt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileframe.R;
import com.mobileframe.tools.ViewTool;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * Author: 10496
 * Date: 2020/4/12 11:07
 * Description: 设置文本progressBar
 * History:
 */
public class ProgressTextBar extends ProgressBar {
    /**
     * 文案前缀
     */
    private String textPrefix = "资源连接中...";
    private String text;
    private Paint mPaint;

    public ProgressTextBar(Context context) {
        super(context);
        initText();
    }

    public ProgressTextBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initText();
    }


    public ProgressTextBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    @Override
    public synchronized void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(this.text, x, y, this.mPaint);
    }

    //初始化，画笔
    private void initText() {
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.font_28px));
        setText(0);
    }

    private void setText(int progress) {
        if (progress > 0 && progress < getMax()) {
            text = textPrefix + progress + "%";
        } else {
            text = textPrefix;
        }
    }

    //设置文字内容
    public void setTextPrefix(String textPrefix) {
        this.textPrefix = textPrefix;
        invalidate();
    }
}


