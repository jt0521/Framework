package com.net.netretrofit;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.net.netretrofit.callback.BaseHttpResponseUi;
import com.net.netretrofit.callback.FileRequestCallback;
import com.net.netretrofit.callback.GeneralRequestCallback;
import com.net.netretrofit.callback.RequestCallback;
import com.net.netretrofit.callback.RetrofitHttpResponseUi;
import com.net.netretrofit.listener.RequestListener;
import com.net.netretrofit.request.Request;

import retrofit2.Call;
import retrofit2.Response;


/**
 * @author tgl
 * http请求管理，可以放入app中
 */
public class RequestManager {
    /**
     * The singleton instance of RequestManagerRetriever.
     */
    private static final RequestManager INSTANCE = new RequestManager();
    /**
     * Main thread handler to handle cleaning up pending fragment maps.
     */
    private final Handler mHandler;

    public static RequestManager getInstance() {
        return INSTANCE;
    }

    private RequestManager() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 异步请求
     *
     * @param context
     * @param call
     * @param listener
     * @param <T>
     */
    public <T> void enqueueRequest(Context context, final Call call, RequestListener<T> listener) {
        enqueueRequest(context, BaseHttpResponseUi.FLAG_SHOW_PROGRESS_DIALOG, call, listener);
    }

    /**
     * 异步请求
     *
     * @param context
     * @param responseFlag {@link BaseHttpResponseUi#FLAG_NONE}{@link BaseHttpResponseUi#FLAG_SHOW_PROGRESS_DIALOG}
     *                     {@link BaseHttpResponseUi#FLAG_PROGRESS_DIALOG_NONCANCELABLE}
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * {@link BaseHttpResponseUi#FLAG_SHOW_LOAD_FAIL_VIEW}{@link BaseHttpResponseUi#FLAG_PROGRESS_MANUAL_CLOSE}
     * @param call
     * @param listener
     * @param <T>
     */
    public <T> void enqueueRequest(Context context, int responseFlag, final Call call,
                                   RequestListener<T> listener) {
        if (listener != null) {
            listener.onPreStart();
        }
        BaseHttpResponseUi ui = new RetrofitHttpResponseUi(context, call, listener);
        ui.setHttpResponseUi(responseFlag);
        ui.onPreStart();


        Request request = new Request(context, mHandler, call, listener);
        request.enqueue(ui);
    }

    /**
     * 异步请求
     * 非业务的接口请求
     *
     * @param context
     * @param responseFlag {@link BaseHttpResponseUi#FLAG_NONE}{@link BaseHttpResponseUi#FLAG_SHOW_PROGRESS_DIALOG}
     *                     {@link BaseHttpResponseUi#FLAG_PROGRESS_DIALOG_NONCANCELABLE}
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * {@link BaseHttpResponseUi#FLAG_SHOW_LOAD_FAIL_VIEW}{@link BaseHttpResponseUi#FLAG_PROGRESS_MANUAL_CLOSE}
     * @param call
     * @param listener
     * @param <T>
     */
    public <T> void enqueueGeneralRequest(Context context, int responseFlag, final Call call,
                                          RequestListener<T> listener) {
        if (listener != null) {
            listener.onPreStart();
        }
        BaseHttpResponseUi ui = new RetrofitHttpResponseUi(context, call, listener);
        ui.setHttpResponseUi(responseFlag);
        ui.onPreStart();


        Request request = new Request(context, mHandler, call);
        GeneralRequestCallback callback = new GeneralRequestCallback();
        request.enqueue(ui, listener, callback);
    }

    /**
     * 异步请求(后台运行)
     *
     * @param call
     * @param listener
     * @param <T>
     */
    public <T> void enqueueRequestInBackground(final Call call, RequestListener<T> listener) {
        if (listener != null) {
            listener.onPreStart();
        }

        RequestCallback callback = new RequestCallback(listener);
        call.enqueue(callback);
    }

    /**
     * 同步请求
     *
     * @param call
     * @param listener
     * @param <T>
     */
    public static <T> void executeRequest(final Call call, RequestListener<T> listener) {
        if (listener != null) {
            listener.onPreStart();
        }

        RequestCallback callback = new RequestCallback(listener);
        try {
            Response response = call.execute();
            callback.onResponse(call, response);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    /**
     * 下载文件
     *
     * @param call
     * @param filePath 保存的文件路径
     * @param listener
     */
    public void download(final Call call, final String filePath,
                         final RequestListener<String> listener) {
        if (listener != null) {
            listener.onPreStart();
        }

        FileRequestCallback callback = new FileRequestCallback(mHandler, filePath, listener);
        call.enqueue(callback);
    }
}
