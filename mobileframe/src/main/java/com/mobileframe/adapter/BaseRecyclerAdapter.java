package com.mobileframe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileframe.interfaces.OnRecyclerViewItemClickListener;
import com.mobileframe.interfaces.OnRecyclerViewItemLongClickListener;
import com.mobileframe.widegt.holderview.BaseRecyclerHolder;

import java.util.List;

/**
 * Created by Administrator on 2016/9/18.
 */
public abstract class BaseRecyclerAdapter<VH extends BaseRecyclerHolder, T> extends RecyclerView.Adapter<VH> {

    public Context mContext;
    private List<T> mListData;
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener;
    public LayoutInflater mInflater;

    public BaseRecyclerAdapter(Context context) {
        this(context, null);
    }

    public BaseRecyclerAdapter(Context context, List<T> listData) {
        mContext = context;
        mListData = listData;
        mInflater = LayoutInflater.from(context);
    }

    public void setListData(List<T> listData) {
        mListData = listData;
    }

    @Override
    public int getItemCount() {
        return mListData == null ? 0 : mListData.size();
    }

    public T getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        onBindViewData(holder, position);
        onItemClickListener(holder);
        onItemLongClickListener(holder);
    }

    public abstract void onBindViewData(VH holder, int position);


    public void setOnItemClickListener(OnRecyclerViewItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public void onItemClickListener(@NonNull final RecyclerView.ViewHolder holder) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
    }

    public void onItemLongClickListener(@NonNull final RecyclerView.ViewHolder holder) {
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemLongClickListener.onItemLongClick(holder.itemView,
                            holder.getLayoutPosition());
                }
            });
        }
    }
}
