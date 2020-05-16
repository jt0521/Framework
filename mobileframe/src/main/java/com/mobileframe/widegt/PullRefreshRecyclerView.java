package com.mobileframe.widegt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileframe.R;


/**
 * description: 自定义刷新view
 * author: tgl
 * date: 2017/6/27 15:34
 * update: 2017/6/27
 */

public class PullRefreshRecyclerView extends RecyclerView {

    private View headerView;
    private View footerView;
    private Context context;

    private RotateAnimation upAnima;
    private RotateAnimation downAnima;

    /**
     * 下拉高度
     */
    private int pullDownHeight;


    public PullRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    /**
     * 返回尾部布局，供外部调用
     * @return
     */
    public View getFooterView(){
        return footerView;
    }
    /**
     * 返回头部布局，供外部调用
     * @return
     */
    public View getHeaderView(){
        return headerView;
    }

    private TextView textTime;
    private TextView textStatus;
    private ImageView ivRefresh;
    private ProgressBar progressBarRefresh;
    private void initHeaderView() {
        headerView = (LinearLayout) View.inflate(context, R.layout.layout_refresh_header, null);
        textTime = (TextView) headerView.findViewById(R.id.tv_time);
        textStatus = (TextView) headerView.findViewById(R.id.tv_status);
        ivRefresh = (ImageView) headerView.findViewById(R.id.iv_header_refresh);
        progressBarRefresh = (ProgressBar) headerView.findViewById(R.id.pb_header_refresh);
        headerView.measure(0, 0);
        pullDownHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0, -pullDownHeight, 0, 0);
        //初始化头部布局的动画
        initAnimation();
    }


    /**
     * 刷新状态改变时的动画
     */
    private void initAnimation() {
//        从下拉刷新状态转换为释放刷新状态
        upAnima = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upAnima.setFillAfter(true);
        upAnima.setDuration(500);
//         转到下拉刷新的动画
        downAnima = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        downAnima.setFillAfter(true);
        downAnima.setDuration(500);
    }

    private float startY = -1 ;
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = e.getAction();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY==-1){
                    startY = e.getY();
                }
                int dy = (int)(getPaddingTop()+(e.getY()-startY));
                setPadding(0,dy,0,0);
                break;
            case MotionEvent.ACTION_UP:
                setPadding(0,-pullDownHeight,0,0);
                startY = -1 ;
                break;
        }
        return super.onTouchEvent(e);
    }
}
