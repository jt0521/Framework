package com.mobileframe.widegt.datapicker.core;

public interface OnWheelPickedListener<B> {
	
	@SuppressWarnings("rawtypes")
	public void onWheelSelected(AbstractWheelPicker wheelPicker, int index, B data);
}
