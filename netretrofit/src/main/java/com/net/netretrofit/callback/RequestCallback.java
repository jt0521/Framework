package com.net.netretrofit.callback;

import android.text.TextUtils;

import com.net.netretrofit.BuildConfig;
import com.net.netretrofit.HttpHeader;
import com.net.netretrofit.JsonUtils;
import com.net.netretrofit.listener.RequestListener;
import com.net.netretrofit.request.LifeCycle;
import com.net.netretrofit.request.Request;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author tgl
 * 普通数据接口回调
 */
public class RequestCallback extends BaseCallback {

    public <T> RequestCallback(Request request, RequestListener<T> listener, LifeCycle lifeCycle) {
        super(request, listener, lifeCycle);
    }

    public <T> RequestCallback(RequestListener<T> listener) {
        super(listener);
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (response != null && response.code() == 200) {
            try {
                ResultBean result = parseResultBean(response);
                if (mRequestListener != null) {
                    switch (result.code) {
                        case ErrorCode.SUCCESS:
                            //保存header
                            HttpHeader.saveResponseHeader(response.headers());

                            mRequestListener.onSuccess(result);
                            mRequestListener.onResponse(response.body());
                            break;
                        case ErrorCode.APP_REQUEST_FREQUENTLY:
                            //do nothing
                            onFailureCallbackDoNothing(result.code, result.msg);
                            //onFailureCallback(result.statusCode, result.msg);
                            CallRequestHelper.onFailure(call, response);
                            break;
                        default:
                            String msg = result.msg;
                            if (TextUtils.isEmpty(msg)) {
                                msg = mDefaultMsg;
                            } else if (msg.contains("exception") || msg.contains("Exception")) {
                                if (!BuildConfig.DEBUG) {
                                    msg = mDefaultMsg;
                                }
                            }
                            onFailureCallback(result.code, msg, false);
                            CallRequestHelper.onFailure(call, response);
                            break;
                    }
                }

                if (mHttpResponseUi != null) {
                    mHttpResponseUi.onSuccess(result.code);
                }
                if (BuildConfig.DEBUG) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("--> ");
                    builder.append(JsonUtils.toJSONString(result));
                    CallRequestHelper.handleLog(builder, null, false);
                }
            } catch (ClassCastException e) {
                String msg = e.getMessage();
                onFailureCallback(ErrorCode.CAST_EX, msg, false);
                CallRequestHelper.onFailure(call, ErrorCode.CAST_EX, e);
            } catch (Exception e) {
                String msg = e.getMessage();
                onFailureCallback(ErrorCode.HTTP_UNSPECIFIC, msg, false);
                CallRequestHelper.onFailure(call, ErrorCode.HTTP_UNSPECIFIC, e);
            }
        } else {
            int code = response == null ? ErrorCode.HTTP_EX : response.code();
            String msg = response == null ? mDefaultMsg : response.message();
            onFailureCallback(code, msg, true);
            CallRequestHelper.onFailure(call, response);
        }

        onLoadComplete();
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        if (call != null && call.isCanceled()) {
            onCancelCallback();
        } else {
            onFailureCallback(ErrorCode.HTTP_EX, mDefaultMsg, true);
        }
        CallRequestHelper.onFailure(call, ErrorCode.HTTP_EX, t);

        onLoadComplete();
    }

    /**
     * 解析ResultBean
     * {@link com.net.netretrofit.callback.ResultBean}
     *
     * @param response
     * @return
     */
    private ResultBean parseResultBean(Response response) {
        ResultBean result;
        Object body = response.body();
        if (body instanceof ResultBean) {
            result = (ResultBean) body;
            result.body = parseData(result.body);
        } else {
            result = JsonUtils.parseObject(body.toString(), ResultBean.class);
            result.body = parseData(result.body);
        }

        return result;
    }
}
