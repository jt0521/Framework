package com.net.netretrofit.request;

import android.app.Activity;
import android.util.Log;

import androidx.fragment.app.Fragment;

import java.util.HashSet;

/**
 * @author tgl
 */
public final class SupportRequestFragment extends Fragment implements LifeCycle {
    private HashSet<RequestLifecycle> mChildRequestFragments;

    public SupportRequestFragment() {
        mChildRequestFragments = new HashSet<RequestLifecycle>();
    }

    @Override
    public void addRequestLifecycle(RequestLifecycle lifecycle) {
        if (lifecycle != null) {
            mChildRequestFragments.add(lifecycle);
        }
    }

    @Override
    public void removeRequestLifecycle(RequestLifecycle lifecycle) {
        if (lifecycle != null) {
            mChildRequestFragments.remove(lifecycle);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mChildRequestFragments != null) {
            for (RequestLifecycle rlc : mChildRequestFragments) {
                rlc.onStop();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mChildRequestFragments != null) {
            for (RequestLifecycle rlc : mChildRequestFragments) {
                rlc.onDestroy();
            }
        }

        super.onDestroy();
    }
}
