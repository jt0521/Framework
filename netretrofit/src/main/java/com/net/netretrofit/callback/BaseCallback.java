package com.net.netretrofit.callback;

import android.text.TextUtils;

import androidx.annotation.CallSuper;

import com.net.netretrofit.JsonUtils;
import com.net.netretrofit.listener.RequestListener;
import com.net.netretrofit.request.LifeCycle;
import com.net.netretrofit.request.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author tgl
 * 网络响应处理基类
 */
public abstract class BaseCallback implements retrofit2.Callback {
    protected RequestListener mRequestListener;
    protected BaseHttpResponseUi mHttpResponseUi;

    protected Request mRequest;
    protected LifeCycle mLifeCycle;
    protected String mDefaultMsg = "数据请求失败！";

    public <T> BaseCallback(Request request, RequestListener<T> listener, LifeCycle lifeCycle) {
        mRequest = request;
        mRequestListener = listener;
        mLifeCycle = lifeCycle;
    }

    public <T> BaseCallback(RequestListener<T> listener) {
        mRequestListener = listener;
    }

    public <T> BaseCallback() {

    }

    @CallSuper
    public void setHttpResponseUi(BaseHttpResponseUi responseUi) {
        mHttpResponseUi = responseUi;
    }

    @CallSuper
    public void setRequest(Request request) {
        mRequest = request;
    }

    @CallSuper
    public void setLifeCycle(LifeCycle lifeCycle) {
        mLifeCycle = lifeCycle;
    }

    @CallSuper
    public void setRequestListener(RequestListener listener) {
        mRequestListener = listener;
    }

    @CallSuper
    public void set(Request request, LifeCycle lifeCycle, BaseHttpResponseUi responseUi, RequestListener listener) {
        mRequest = request;
        mRequestListener = listener;
        mHttpResponseUi = responseUi;
        mLifeCycle = lifeCycle;
    }


    /**
     * 取消处理
     */
    protected void onCancelCallback() {
        if (mHttpResponseUi != null) {
            mHttpResponseUi.onCancelled();
        }
        if (mRequestListener != null) {
            mRequestListener.onCancelled();
        }
    }


    /**
     * 失败处理
     *
     * @param code
     * @param msg
     * @param httpError 是否是http请求错误
     */
    protected void onFailureCallback(int code, String msg, boolean httpError) {
        boolean showToast = false;
        if (mRequestListener != null) {
            showToast = !mRequestListener.onFailure(code, msg, httpError);
        }

        if (mHttpResponseUi != null) {
            mHttpResponseUi.onFailure(code, msg, httpError, showToast);
        }
    }

    /**
     * 失败，业务逻辑什么都不处理，只处理加载进度框
     *
     * @param code
     * @param msg
     */
    protected void onFailureCallbackDoNothing(int code, String msg) {
        if (mHttpResponseUi != null) {
            mHttpResponseUi.onFailureDoThing(code, msg);
        }
    }

    /**
     * 加载完成
     */
    protected void onLoadComplete() {
        if (mRequestListener != null) {
            mRequestListener.onLoadComplete();
        }
        if (mLifeCycle != null && mRequest != null) {
            mLifeCycle.removeRequestLifecycle(mRequest);
        }
    }

    /**
     * 解析data数据
     *
     * @param data
     * @return
     */
    protected Object parseData(Object data) {
        if (data == null || TextUtils.isEmpty(data.toString())) {
            return null;
        }

        Type type = null;
        try {
            Type[] dataTypes = mRequestListener != null ? ((ParameterizedType) mRequestListener.getClass()
                    .getGenericSuperclass()).getActualTypeArguments() : null;
            type = dataTypes != null && dataTypes.length > 0 ? dataTypes[0] : null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (JsonUtils.isCanParsableJsonObject(type, data)) {
            data = JsonUtils.parseObject(data.toString(), type);
        }

        return data;
    }

}
