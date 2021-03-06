package com.mobileframe.widegt.pulltorefresh;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.mobileframe.tools.FileUtils;

import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 功能描述：带有缓存的数据适配器 首先读取本地缓存，再从网络请求数据(不支持分页,不支持下拉刷新)
 * <p>
 * 修改人： 修改描述： 修改日期
 *
 * @param <B> 数据对象bean
 * @param <H> item对象 holder
 */
public abstract class AbsSwipeCacheAdapter<B, H> extends DataSwipeAdapter<B, H> {
    private static final int REQUEST_FAILED = -1;
    private static final int REQUEST_SUCC = 0;

    private String mCacheRequestId;
    private String mHttpRequestId;
    private String mCachePath;

    private OnDataLoadListener mDataLoadListener;

    public AbsSwipeCacheAdapter(Context context,
                                PullToRefreshSwipeListView listView,
                                AbsSwipeCacheRequestParams requestParams) {
        super(context, listView);
        mCachePath = requestParams.cachePath;
        mDataLoadListener = requestParams.dataLoadListener;

        // 禁止滑动刷新
        mListView.setMode(PullToRefreshBase.Mode.DISABLED);

        initData(mCachePath);
    }

    /**
     * 优先从本地缓存取，从服务器读取后再刷新当前页面
     *
     * @param cachePath
     */
    private void initData(final String cachePath) {
        mCacheRequestId = UUID.randomUUID().toString();
        if (mDataLoadListener != null) {
            mDataLoadListener.onDataLoadStrat(mCacheRequestId);
        }

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return FileUtils.readFile(cachePath);
            }

            @Override
            protected void onPostExecute(String result) {
                if (!TextUtils.isEmpty(result)) {
                    // 存在缓存，先用缓存刷新界面
                    List<B> data = onGetDataSucc(result);
                    if (data != null && !data.isEmpty()) {
                        fillData(onGetDataSucc(result));
                    }
                }

                // 从服务器上获取新数据
                mHttpRequestId = getDataFromSever();
                if (mDataLoadListener != null) {
                    mDataLoadListener.onHttpLoading(mHttpRequestId);
                }
            }

        }.execute();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (mDataLoadListener != null) {
                mDataLoadListener.onDataLoadFinish(mCacheRequestId,
                        mHttpRequestId);
            }

            // 删除多余的EmptyView
            removeEmptyView();

            // 刷新界面数据
            if (msg.arg1 == REQUEST_FAILED) {
                addFailedView();
            } else {
                String result = (String) msg.obj;
                fillData(onGetDataSucc(result));
            }
        }
    };

    private String getDataFromSever() {
        //TODO 此处需要修正
        //return initRequest();
        return System.currentTimeMillis() + "";
    }

    @Override
    protected void doRequest() {
        getDataFromSever();
    }

    @Override
    public void doRefresh() {
        initData(mCachePath);
    }

    private void removeEmptyView() {
        mListView.removeEmptyView(getEmptyView());
        mListView.removeEmptyView(getFailedView());
    }

    private void fillData(List<B> datas) {
        boolean empty = datas == null || datas.isEmpty();
        if (mDataLoadListener != null) {
            mDataLoadListener.onDataLoaded(!empty);
        }

        if (empty) {
            //addEmptyView();
        } else {
            setListData(datas, true);
        }
    }


    public abstract List<B> onGetDataSucc(String result);

    public interface OnDataLoadListener {
        public void onDataLoadStrat(String handlerId);

        public void onHttpLoading(String httpRequestId);

        public void onDataLoadFinish(String cacheRequestId, String httpRequestId);

        public void onDataLoaded(boolean hasDatas);
    }
}
