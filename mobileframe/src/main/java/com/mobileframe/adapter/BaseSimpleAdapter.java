package com.mobileframe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobileframe.widegt.holderview.BaseHolderView;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 重写BaseAdapter，进一步简化
 * author: tgl
 * date: 2017/7/5 16:27
 * update: 2017/7/5
 */

public abstract class BaseSimpleAdapter<H, T> extends BaseAdapter {

    protected Context mContext;
    private List<T> mList;
    private LayoutInflater inflater;

    public BaseSimpleAdapter(Context context) {
        this(context, new ArrayList<T>());
    }

    public BaseSimpleAdapter(Context context, List<T> list) {
        mContext = context;
        this.mList = list;
        inflater = LayoutInflater.from(context);
    }

    public abstract int getLayoutId();

    /**
     * 多种布局时返回余下布局Id
     * 第一布局viewType默认为0
     * 请按int逐增返回viewType
     *
     * @return
     */
    public int[] getLayoutMoreId() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        if (getLayoutMoreId() == null) {
            return 1;
        }
        return getLayoutMoreId().length + 1;
    }

    public void setList(List<T> list) {
        mList = list;
    }

    public void addList(List<T> list) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        if (list != null) {
            mList.addAll(list);
        }
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H holder;
        int viewType = getItemViewType(position);
        if (convertView == null) {
            if (viewType == 0) {
                convertView = inflater.inflate(getLayoutId(), parent, false);
            } else {
                convertView = inflater.inflate(getLayoutMoreId()[viewType - 1], parent, false);
            }
            holder = createHolder(convertView, viewType);
            convertView.setTag(holder);
        } else {
            holder = (H) convertView.getTag();
        }
        refreshViewData(holder, position, getItem(position));
        return convertView;
    }

    /**
     * 刷新view数据
     *
     * @param holder
     */
    public abstract void refreshViewData(H holder, int position, T data);

    public abstract H createHolder(View convertView, int viewType);
}
