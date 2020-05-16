package com.mobileframe.widegt.holderview;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.ButterKnife;

/**
 * author: tgl
 * 简化 recyclerView ViewHolder
 */

public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    public BaseRecyclerHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(itemView);
    }

    public <T extends View> T findViewById(@IdRes int viewId) {
        return itemView.findViewById(viewId);
    }

}
