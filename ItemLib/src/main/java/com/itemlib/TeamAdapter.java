package com.itemlib;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Copyright (C), 2016-2020,呼我出行网络科技有限公司
 * FileName: TeamAdapter
 * Author: Administrator
 * Date: 2020/10/29 15:51
 * Description:
 * History:
 */
public abstract class TeamAdapter<T, TVH extends RecyclerView.ViewHolder
        , CVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    private Context mContext;
    private List<T> mTeams;
    private int mCount;
    private final int TYPE_TEAM = 1;
    private final int TYPE_CHILD = 2;
    private final int INVALID_POSITION = -1;
    private OnTeamClickListener onTeamClickListener;
    private OnChildClickListener onChildClickListener;

    public void setOnTeamClickListener(OnTeamClickListener onTeamClickListener) {
        this.onTeamClickListener = onTeamClickListener;
    }

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        this.onChildClickListener = onChildClickListener;
    }

    public TeamAdapter(Context context) {
        mContext = context;
    }

    public void setTeams(List<T> mTeams) {
        this.mTeams = mTeams;
        getCount();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEAM) {
            TVH holder = onCreateTeamHolder(parent, viewType);
            if (onTeamClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        Position pos = getTeamChildPosition(position);
                        onTeamClickListener.onTeamItemClick(v, pos.team);
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
        if (getItemViewType(position) == TYPE_TEAM) {
            onBindViewTeamHolder(holder, position);
        } else {
            onBindViewChildHolder(holder, position);
        }
    }

    public abstract void onBindViewTeamHolder(@NonNull RecyclerView.ViewHolder holder, int position);

    public abstract void onBindViewChildHolder(@NonNull RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        return getItemType(position) == ItemType.TYPE_TEAM_TITLE ? TYPE_TEAM : TYPE_CHILD;
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    private int getCount() {
        if (mTeams == null) {
            return mCount = 0;
        }
        mCount = 0;
        for (T t : mTeams) {
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
        for (T t : mTeams) {
            if (itemPosition == count) {
                return ItemType.TYPE_TEAM_TITLE;
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
        for (T g : mTeams) {
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
