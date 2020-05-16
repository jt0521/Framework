package com.mobileframe.tools;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * Author: 10496
 * Date: 2020/4/5 17:20
 * Description: 软件盘管理
 * History:
 */
public class InputMethodManagerUtil {
    /**
     * 隐藏软键盘
     *
     * @param view view
     */
    public static void hideSoftInput(View view) {
        if (view != null) {
            InputMethodManager inputManger = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputManger.isActive()) {
                inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 显示输入法
     *
     * @return
     */
    public static void showIm(View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }
}
