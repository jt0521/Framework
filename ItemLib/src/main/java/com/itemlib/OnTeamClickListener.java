/*
 * Copyright (c) 2018. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.itemlib;

import android.view.View;

/**
 * 组点击事件
 */
public interface OnTeamClickListener {
    /**
     * Callback when the group item was clicked.
     *
     * @param itemView      the itemView of the group item.
     * @param groupPosition the position of the group.
     */
    void onTeamItemClick(View itemView, int groupPosition);
}
