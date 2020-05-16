package com.net.netretrofit.request;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.net.netretrofit.callback.BaseCallback;
import com.net.netretrofit.callback.BaseHttpResponseUi;
import com.net.netretrofit.callback.RequestCallback;
import com.net.netretrofit.listener.RequestListener;
import com.net.netretrofit.tool.Util;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * @author tgl
 * 网络请求生命周期处理
 */
public class Request implements RequestLifecycle, Handler.Callback {
    private static final String TAG = "Request";
    private static final String FRAGMENT_TAG = "com.mobileframe.manager";

    private static final int ID_REMOVE_FRAGMENT = 1;
    private static final int ID_REMOVE_SUPPORT_FRAGMENT = 2;

    private Handler mHandler;
    private LifeCycle mLifeCycle;

    /**
     * retrofit call
     */
    private Call mCall;
    /**
     * http callback
     */
    private RequestListener mListener;

    /**
     * Pending adds for RequestManagerFragments.
     */
    final Map<FragmentManager, RequestFragment> mPendingRequestFragments =
            new HashMap<FragmentManager, RequestFragment>();

    /**
     * Pending adds for SupportRequestManagerFragments.
     */
    final Map<androidx.fragment.app.FragmentManager, SupportRequestFragment> mPendingSupportRequestFragments =
            new HashMap();

    /**
     * constructor
     *
     * @param context
     * @param handler
     * @param call
     * @param listener
     */
    public <T> Request(Context context, Handler handler, final Call call,
                       RequestListener<T> listener) {
        mHandler = handler;

        mCall = call;
        mListener = listener;

        get(context);
    }

    /**
     * constructor
     *
     * @param context
     * @param handler
     * @param call
     */
    public <T> Request(Context context, Handler handler, final Call call) {
        mHandler = handler;
        mCall = call;
        get(context);
    }

    public void get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (Util.isOnMainThread() && !(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                get((FragmentActivity) context);
            } else if (context instanceof Activity) {
                get((Activity) context);
            } else if (context instanceof ContextWrapper) {
                get(((ContextWrapper) context).getBaseContext());
            }
        }
    }

    public void get(FragmentActivity activity) {
        if (Util.isOnBackgroundThread()) {
            get(activity.getApplicationContext());
        } else {
            //assertNotDestroyed(activity);
            androidx.fragment.app.FragmentManager fm = activity.getSupportFragmentManager();
            LifeCycle rq = getSupportRequestManagerFragment(fm);
            rq.addRequestLifecycle(this);
        }
    }

    public void get(Fragment fragment) {
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a load on a fragment before it " +
                    "is attached");
        }
        if (Util.isOnBackgroundThread()) {
            get(fragment.getActivity().getApplicationContext());
        } else {
            androidx.fragment.app.FragmentManager fm = fragment.getChildFragmentManager();
            LifeCycle rq = getSupportRequestManagerFragment(fm);
            rq.addRequestLifecycle(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void get(Activity activity) {
        if (Util.isOnBackgroundThread() || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            get(activity.getApplicationContext());
        } else {
            //assertNotDestroyed(activity);
            FragmentManager fm = activity.getFragmentManager();
            LifeCycle rq = getRequestManagerFragment(fm);
            rq.addRequestLifecycle(this);
        }
    }

    /*@TargetApi(17)
    private static void assertNotDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }*/

    @TargetApi(17) //Build.VERSION_CODES.JELLY_BEAN_MR1
    public void get(android.app.Fragment fragment) {
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a load on a fragment before it " +
                    "is attached");
        }
        if (Util.isOnBackgroundThread() || Build.VERSION.SDK_INT < 17) {
            get(fragment.getActivity().getApplicationContext());
        } else {
            FragmentManager fm = fragment.getChildFragmentManager();
            LifeCycle rq = getRequestManagerFragment(fm);
            rq.addRequestLifecycle(this);
        }
    }

    @TargetApi(17) //Build.VERSION_CODES.JELLY_BEAN_MR1
    public LifeCycle getRequestManagerFragment(final FragmentManager fm) {
        RequestFragment current = (RequestFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = mPendingRequestFragments.get(fm);
            if (current == null) {
                current = new RequestFragment();
                mPendingRequestFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
                mHandler.obtainMessage(ID_REMOVE_FRAGMENT, fm).sendToTarget();
            }
        }
        mLifeCycle = current;

        return current;
    }

    public LifeCycle getSupportRequestManagerFragment(final androidx.fragment.app.FragmentManager fm) {
        SupportRequestFragment current =
                (SupportRequestFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = mPendingSupportRequestFragments.get(fm);
            if (current == null) {
                current = new SupportRequestFragment();
                mPendingSupportRequestFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
                mHandler.obtainMessage(ID_REMOVE_SUPPORT_FRAGMENT, fm).sendToTarget();
            }
        }

        mLifeCycle = current;
        return current;
    }

    @Override
    public boolean handleMessage(Message message) {
        boolean handled = true;
        Object removed = null;
        Object key = null;
        switch (message.what) {
            case ID_REMOVE_FRAGMENT:
                FragmentManager fm = (FragmentManager) message.obj;
                key = fm;
                removed = mPendingRequestFragments.remove(fm);
                break;
            case ID_REMOVE_SUPPORT_FRAGMENT:
                androidx.fragment.app.FragmentManager supportFm =
                        (androidx.fragment.app.FragmentManager) message.obj;
                key = supportFm;
                removed = mPendingSupportRequestFragments.remove(supportFm);
                break;
            default:
                handled = false;
        }
        if (handled && removed == null && Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, "Failed to remove expected request manager fragment, manager: " + key);
        }
        return handled;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        if (mCall != null) {
            mCall.cancel();
            Log.i(TAG, "===============call cancel===========");
        }

        //Log.i(TAG, "call:" + mCall.isCanceled());
    }

    public void enqueue() {
        if (mCall != null) {
            mCall.enqueue(new RequestCallback(this, mListener, mLifeCycle));
        }
    }

    public void enqueue(BaseHttpResponseUi responseUi) {
        if (mCall != null) {
            RequestCallback callback = new RequestCallback(this, mListener, mLifeCycle);
            callback.setHttpResponseUi(responseUi);
            mCall.enqueue(callback);
        }
    }

    public void enqueue(BaseHttpResponseUi responseUi, RequestListener listener,
                        BaseCallback callback) {
        if (mCall != null) {
            callback.setHttpResponseUi(responseUi);
            callback.setRequest(this);
            callback.setLifeCycle(mLifeCycle);
            callback.setRequestListener(listener);
            mCall.enqueue(callback);
        }
    }

}
