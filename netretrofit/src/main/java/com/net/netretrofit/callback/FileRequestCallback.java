package com.net.netretrofit.callback;

import android.os.Handler;
import com.net.netretrofit.tool.FileUtil;
import com.net.netretrofit.listener.RequestListener;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author tgl
 * 文件下载回调
 */
public class FileRequestCallback implements Callback {
    private RequestListener mListener;
    private Handler mHandler;
    private String mFilePath;

    public <T> FileRequestCallback(Handler handler, String filePath, RequestListener<T> listener) {
        mHandler = handler;
        mFilePath = filePath;
        mListener = listener;
    }

    @Override
    public void onResponse(final Call call, final Response response) {
        final boolean httpError = response == null;
        if (response == null || response.code() != 200) {
            onFailureCallback(httpError ? ErrorCode.HTTP_EX : response.code(), "文件下载失败", httpError);
            CallRequestHelper.onFailure(call, response);
            onLoadComplete();
            return;
        }
        if (mHandler == null || mListener == null) {
            onLoadComplete();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                final boolean success = FileUtil.writeFile(mFilePath,
                        ((ResponseBody) response.body()).byteStream());
                if (mHandler != null) {
//                    Message msg = mHandler.obtainMessage();
//                    msg.obj = success;
//                    mHandler.sendMessage(msg);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (success) {
                                mListener.onSuccess(mFilePath);
                            } else {
                                onFailureCallback(ErrorCode.FILE_DOWNLOAD_FAIL, "文件下载失败!",
                                        httpError);
                                CallRequestHelper.onFailure(call, response);
                            }
                            onLoadComplete();
                        }
                    });
                }
            }
        }.start();

    }

    @Override
    public void onFailure(Call call, Throwable t) {
        onFailureCallback(ErrorCode.HTTP_EX, "文件下载失败", true);
        CallRequestHelper.onFailure(call, ErrorCode.HTTP_EX, t);

        onLoadComplete();
    }

    /**
     * 失败处理
     *
     * @param code
     * @param msg
     */
    private void onFailureCallback(int code, String msg, boolean httpError) {
        if (mListener != null) {
            mListener.onFailure(code, msg, httpError);
        }
    }

    /**
     * 加载完成
     */
    private void onLoadComplete() {
        if (mListener != null) {
            mListener.onLoadComplete();
        }
    }
}
