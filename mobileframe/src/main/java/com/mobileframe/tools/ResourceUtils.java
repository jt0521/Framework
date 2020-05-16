package com.mobileframe.tools;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;

/**
 * Created by Administrator on 2016/9/6.
 */
public class ResourceUtils {

    /**
     * @param context
     * @param resName 图片资源名称
     * @return
     */
    public static int getImgResourceId(Context context, @DrawableRes String resName) {
        if (TextUtils.isEmpty(resName) || "-".equals(resName)) {
            return 0;
        }
        try {
            int resId = context.getResources().getIdentifier(resName, "mipmap", context.getPackageName());
            return resId;
        } catch (Exception ex) {
        }
        return 0;
    }

    /**
     * @param prefixName 前缀
     * @param name
     */
    public static int getImgResourceId(Context context, String prefixName, String name) {
        String resName = prefixName + name;
        try {
            int resId = ResourceUtils.getImgResourceId(context, resName);
            return resId;
        } catch (Exception ex) {

        }
        return 0;
    }

    /**
     * @param prefixName   前缀
     * @param name
     * @param defaultResId 默认图片
     */
    public static int getImgResourceId(Context context, String prefixName, String name, int defaultResId) {
        String resName = prefixName + name;
        try {
            int resId = ResourceUtils.getImgResourceId(context, resName);
            if (resId <= 0) {
                return defaultResId;
            }
            return resId;
        } catch (Exception ex) {

        }
        return defaultResId;
    }

    /**
     * 获取目标图片
     *
     * @param context
     * @param name       图片名称
     * @param targetSize 目标图片大小
     * @return
     */
    public static Bitmap getResourceBitmap(Context context, String name, int targetSize) {
        int resId = ResourceUtils.getImgResourceId(context, name);
        Bitmap bitmap = null;
        if (resId > 0) {
            bitmap = ImageUtils.decodeStream2Resource(context, resId, targetSize, targetSize);
        }
        return bitmap;
    }

    /**
     * 代码生成selector文件
     *
     * @param context
     * @param selectResId   选中时展示的图片资源
     * @param unSelectResId 未选中或默认时展示的图片资源
     * @return
     */
    public static StateListDrawable getSelector(Context context,
                                                @DrawableRes int unSelectResId,
                                                @DrawableRes int selectResId) {
        return getSelector(context, unSelectResId, selectResId, -1, -1, -1);
    }

    /**
     * 代码生成selector文件
     *
     * @param context
     * @param idNormal
     * @param idPressed
     * @param idFocused
     * @param idUnable
     * @return
     */
    private static StateListDrawable getSelector(Context context, int idNormal,
                                                 int idSelect, int idPressed, int idFocused, int idUnable) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : ContextCompat.getDrawable(context, idNormal);
        Drawable select = idSelect == -1 ? null : ContextCompat.getDrawable(context, idSelect);
        Drawable pressed = idPressed == -1 ? null : ContextCompat.getDrawable(context, idPressed);
        Drawable focused = idFocused == -1 ? null : ContextCompat.getDrawable(context, idFocused);
        Drawable unable = idUnable == -1 ? null : ContextCompat.getDrawable(context, idUnable);
        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_selected}, select);
        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_pressed,
                android.R.attr.state_enabled}, pressed);
        // View.ENABLED_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled,
                android.R.attr.state_focused}, focused);
        // View.ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        // View.FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_focused}, focused);
        // View.WINDOW_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_window_focused}, unable);
        // View.EMPTY_STATE_SET
        bg.addState(new int[]{}, normal);
        return bg;
    }

    public static ColorStateList getStateListColor(Context context, @ColorRes int normalResId,
                                                   @ColorRes int pressedResId) {
        final int[][] states = {
                new int[]{android.R.attr.state_enabled,
                        android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_enabled}};
        int[] colors = {
                ContextCompat.getColor(context, pressedResId),
                ContextCompat.getColor(context, pressedResId),
                ContextCompat.getColor(context, normalResId)};
        ColorStateList list = new ColorStateList(states, colors);
        return list;
    }
}
