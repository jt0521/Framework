package com.mobileframe.widegt.datapicker.view;

import android.content.Context;
import android.util.AttributeSet;

import com.mobileframe.widegt.datapicker.core.ScrollWheelPicker;

public abstract class AbstractViewWheelPicker extends ScrollWheelPicker<ViewBaseAdapter> {

	public AbstractViewWheelPicker(Context context) {
		super(context);
	}

	public AbstractViewWheelPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AbstractViewWheelPicker(Context context, AttributeSet attrs,
                                   int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

}
