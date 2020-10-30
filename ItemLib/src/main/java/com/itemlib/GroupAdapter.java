package com.itemlib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Copyright (C), 2016-2020,呼我出行网络科技有限公司
 * FileName: TeamAdapter
 * Author: Administrator
 * Date: 2020/10/29 15:51
 * Description: 分组列表，内容格式为List<T<C>>
 * History:
 */
public abstract class GroupAdapter<T, TVH extends RecyclerView.ViewHolder
        , CVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    private Context mContext;
    private List<T> mGroups;
    private int mCount;
    private final int TYPE_TEAM = 1;
    private final int TYPE_CHILD = 2;
    private final int INVALID_POSITION = -1;
    private OnGroupClickListener onGroupClickListener;
    private OnChildClickListener onChildClickListener;

    public void setOnGroupClickListener(OnGroupClickListener onGroupClickListener) {
        this.onGroupClickListener = onGroupClickListener;
    }

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        this.onChildClickListener = onChildClickListener;
    }

    public GroupAdapter(Context context) {
        mContext = context;
    }

    public View inflate(@LayoutRes int layoutId) {
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }

    public void setData(List<T> datas) {
        this.mGroups = datas;
        getCount();
        notifyDataSetChanged();
    }

    public void add(List<T> datas) {
        int lastCount = getItemCount();
        addGroups(datas);
        getCount();
        notifyItemRangeInserted(lastCount, mCount - lastCount);
    }

    public void update(List<T> groups) {
        mGroups.clear();
        addGroups(groups);
        getCount();
        notifyDataSetChanged();
    }

    private void addGroups(List<T> groups) {
        if (groups != null) {
            mGroups.addAll(groups);
        }
    }

    public T getItem(int position) {
        return mGroups.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEAM) {
            TVH holder = onCreateTeamHolder(parent, viewType);
            if (onGroupClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        Position pos = getTeamChildPosition(position);
                        onGroupClickListener.onTeamItemClick(v, pos.team);
                    }
                });
            }
            return holder;
        }
        CVH childHolder = onCreateChildHolder(parent, viewType);
        if (onChildClickListener != null) {
            childHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = childHolder.getAdapterPosition();
                    Position pos = getTeamChildPosition(position);
                    onChildClickListener.onChildClick(v, pos.team, pos.child);
                }
            });
        }
        return childHolder;
    }

    public abstract TVH onCreateTeamHolder(ViewGroup parent, int viewType);

    public abstract CVH onCreateChildHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Position pos = getTeamChildPosition(position);
        if (getItemViewType(position) == TYPE_TEAM) {
            onBindViewTeamHolder((TVH) holder, pos.team);
        } else {
            onBindViewChildHolder((CVH) holder, pos.team, pos.child);
        }
    }

    public abstract void onBindViewTeamHolder(@NonNull TVH holder, int teamPosition);

    public abstract void onBindViewChildHolder(@NonNull CVH holder, int teamPosition, int position);

    @Override
    public int getItemViewType(int position) {
        return getItemType(position) == ItemType.TYPE_Group_TITLE ? TYPE_TEAM : TYPE_CHILD;
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    private int getCount() {
        if (mGroups == null) {
            return mCount = 0;
        }
        mCount = 0;
        for (T t : mGroups) {
            if (t == null) {
                continue;
            }
            mCount += 1;
            mCount += getChildCount(t);
        }

        return mCount;
    }

    /**
     * 子项数量
     *
     * @param t
     * @return
     */
    public abstract int getChildCount(T t);

    public ItemType getItemType(int itemPosition) {
        int count = 0;
        int childCount;
        for (T t : mGroups) {
            if (itemPosition == count) {
                return ItemType.TYPE_Group_TITLE;
            }
            childCount = getChildCount(t);
            count += 1;
            if (itemPosition == count && childCount != 0) {
                return ItemType.TYPE_CHILD_FIRST;
            }
            count += childCount;
            if (itemPosition < count) {
                return ItemType.TYPE_CHILD_NOT_FIRST;
            }
        }
        throw new IllegalStateException("Could not find item type for item position " + itemPosition);
    }

    public Position getTeamChildPosition(int itemPosition) {
        int itemCount = 0;
        int childCount;
        final Position position = new Position();
        for (T g : mGroups) {
            if (itemPosition == itemCount) {
                position.child = INVALID_POSITION;
                return position;
            }
            itemCount++;
            childCount = getChildCount(g);
            if (childCount > 0) {
                position.child = itemPosition - itemCount;
                if (position.child < childCount) {
                    return position;
                }
                itemCount += childCount;
            }
            position.team++;
        }
        return position;
    }
}
