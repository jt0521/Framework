package com.x.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tgl on 2017/10/24.
 * Describe:
 */

public abstract class LoopVpAdapter<T> extends PagerAdapter implements ViewPager.OnPageChangeListener {

    //    当前页面
    private int currentPosition = 0;
    protected Context mContext;
    protected List<View> views;
    protected ViewPager mViewPager;
    protected LayoutInflater inflater;

    public LoopVpAdapter(Context context, List<T> datas, ViewPager viewPager) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        views = new ArrayList<>();
        if (datas != null && datas.size() != 0) {
//        如果数据大于一条
            if (datas.size() > 1) {
////            添加最后一页到第一页
//                datas.add(0, datas.get(datas.size() - 1));
////            添加第一页(经过上行的添加已经是第二页了)到最后一页
//                datas.add(datas.get(1));
                views.add(getItemView(datas.get(datas.size() - 1)));
                views.add(getItemView(datas.get(1)));
            }
            for (T data : datas) {
                views.add(getItemView(data));
            }
            mViewPager = viewPager;
            viewPager.setAdapter(this);
            viewPager.addOnPageChangeListener(this);
            viewPager.setCurrentItem(1, false);
        }
    }

    protected abstract View getItemView(T data);

    @Override
    public int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        若viewpager滑动未停止，直接返回
        if (state != ViewPager.SCROLL_STATE_IDLE) return;
//        若当前为第一张，设置页面为倒数第二张
        if (currentPosition == 0) {
            mViewPager.setCurrentItem(views.size() - 2, false);
        } else if (currentPosition == views.size() - 1) {
//        若当前为倒数第一张，设置页面为第二张
            mViewPager.setCurrentItem(1, false);
        }
    }


    private boolean isPlay = false;
    private long millis;
    private static final int WHAT = 0;
    /**
     * 是否带有循环
     */
    private boolean isCirculate = true;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if (msg.what == 0 && isCirculate) {
                removeCallbacksAndMessages(null);
                int count = getCount();
                if (count > 2) { // 实际上，多于1个，就多于3个
                    int index = mViewPager.getCurrentItem();
                    index = index % (count - 2) + 1; //这里修改过
                    mViewPager.setCurrentItem(index, true);
                }
                sendEmptyMessageDelayed(WHAT, millis);
            }
        }
    };

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (isCirculate) {
//            switch(ev.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    removeHandlerMessage(WHAT);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    if (isPlay) {
//                        removeHandlerMessage(WHAT);
//                        handler.sendEmptyMessageDelayed(WHAT, millis);
//                    }
//                    break;
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    /**
     * 开启滚动
     *
     * @param millis
     */
    public void startPlay(long millis) {
        if (!this.isPlay && isCirculate) {
            this.isPlay = true;
            this.millis = millis;
            handler.sendEmptyMessageDelayed(WHAT, millis);
        }
    }

    /**
     * 关闭滚动
     */
    public void closePlay() {
        this.isPlay = false;
        removeHandlerMessage(WHAT);
    }

    /**
     * 移除未发送的消息
     *
     * @param what
     */
    private void removeHandlerMessage(int what) {
        if (handler.hasMessages(what)) {
            handler.removeMessages(what);
        }
    }

    public void setCirculate(boolean isCirculate) {
        this.isCirculate = isCirculate;
    }

}