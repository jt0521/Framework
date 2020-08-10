package com.mobileframe.widegt.datapicker.view;

import com.mobileframe.widegt.datapicker.AreaData;

import java.util.List;

public class AreaWheelPickerAdapter extends TextBaseAdapter {
    private List<AreaData> mData;

    public AreaWheelPickerAdapter() {
        //empty constructor
    }

    public AreaWheelPickerAdapter(List<AreaData> data) {
        mData = data;
    }

    public void setData(List<AreaData> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public String getItemText(int position) {
        return mData == null ? null : mData.get(position).getName();
    }

    @Override
    public AreaData getItem(int position) {
        return mData.get(position);
    }
}
