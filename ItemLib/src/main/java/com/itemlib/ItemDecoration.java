package com.itemlib;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 分组列表的分割线
 *
 * @author 黄浩杭 (msdx.android@qq.com)
 * @since 2017-05-02 0.1
 */
public class ItemDecoration extends RecyclerView.ItemDecoration {
    private BaseGroupAdapter mAdapter;
    private Drawable mGroupDivider;
    private Drawable mChildHeaderDivider;
    private Drawable mChildDivider;
    private boolean mFirstDividerEnabled = true;
    private final Rect mBounds = new Rect();

    public ItemDecoration(BaseGroupAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * 组与组的分割线
     *
     * @param groupDivider
     */
    public void setGroupDivider(Drawable groupDivider) {
        mGroupDivider = groupDivider;
    }

    /**
     * child 与title之间的分割线
     *
     * @param titleDivider
     */
    public void setChildHeaderDivider(Drawable titleDivider) {
        mChildHeaderDivider = titleDivider;
    }

    /**
     * child列表分割线
     *
     * @param childDivider
     */
    public void setChildDivider(Drawable childDivider) {
        mChildDivider = childDivider;
    }

    public void setFirstDividerEnabled(boolean enabled) {
        mFirstDividerEnabled = enabled;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (!mFirstDividerEnabled) {
            if (position == 0) {
                return;
            }
        }

        Drawable drawable = getDividerDrawable(position);
        if (drawable != null) {
            outRect.top = drawable.getIntrinsicHeight();
        }
        view.setTag(R.id.item_divider, drawable);
    }

    @Nullable
    private Drawable getDividerDrawable(int position) {
        ItemType type = mAdapter.getItemType(position);
        switch (type) {
            case TYPE_Group_TITLE:
                return mGroupDivider;
            case TYPE_CHILD_FIRST:
                return mChildHeaderDivider;
            case TYPE_CHILD_NOT_FIRST:
                return mChildDivider;
            default:
                return null;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }
        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            Object object = child.getTag(R.id.item_divider);
            if (object != null && object instanceof Drawable) {
                Drawable drawable = (Drawable) object;
                final int top = mBounds.top + Math.round(child.getTranslationX());
                final int bottom = mBounds.top + drawable.getIntrinsicHeight();
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(canvas);
            }
        }
        canvas.restore();
    }
}
