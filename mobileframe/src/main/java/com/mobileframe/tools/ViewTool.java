package com.mobileframe.tools;

import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.FontRes;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * Author: 10496
 * Date: 2020/2/15 12:42
 * Description: View常用操作类
 * History:
 */
public class ViewTool {

    /**
     * 这只文字大小
     *
     * @param textView
     * @param dimenPx
     */
    public static void setTextSize(TextView textView, int dimenPx) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                textView.getContext().getResources().getDimensionPixelSize(dimenPx));
    }
}
