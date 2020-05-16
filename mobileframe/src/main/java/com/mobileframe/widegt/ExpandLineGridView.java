package com.mobileframe.widegt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.mobileframe.tools.DensityUtil;

public class ExpandLineGridView extends GridView {

    private Paint mPaint;

    public ExpandLineGridView(Context context) {
        super(context);
        initPaint();
    }

    public ExpandLineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public ExpandLineGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#F2F3F6"));
        mPaint.setStrokeWidth(DensityUtil.dip2px(getContext(), 0.6f));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //子view的总数
        int childTotalCount = getChildCount();
        //列数
        int columnCount = getNumColumns();
        //行数
        int rowCount;
        if (childTotalCount % columnCount == 0) {
            rowCount = childTotalCount / columnCount;
        } else {
            rowCount = (childTotalCount / columnCount) + 1; //当余数不为0时，要把结果加上1
        }

        for (int i = 0; i < childTotalCount; i++) {//遍历子view
            //画列
            View cellView = getChildAt(i);//获取子view
            canvas.drawLine(cellView.getRight(), cellView.getTop(),
                    cellView.getRight(), cellView.getBottom(), mPaint);

            if (i >= columnCount) {
                //画横线
                canvas.drawLine(cellView.getLeft(), cellView.getTop(),
                        cellView.getRight(), cellView.getTop(), mPaint);
            }
        }
    }
}
