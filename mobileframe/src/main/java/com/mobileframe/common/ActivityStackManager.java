package com.mobileframe.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.LinkedList;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
public class ActivityStackManager {

    private static LinkedList<Activity> activityStack;
    private static ActivityStackManager instance;

    private ActivityStackManager() {
    }

    /**
     * 单一实例
     */
    public static ActivityStackManager getInstance() {
        if (instance == null) {
            synchronized (ActivityStackManager.class) {
                if (instance == null) {
                    instance = new ActivityStackManager();
                }
            }
        }
        return instance;
    }

    /**
     * 获得activity堆栈
     *
     * @return
     */
    private LinkedList<Activity> getActivityStack() {
        if (activityStack == null) {
            activityStack = new LinkedList<Activity>();
        }
        return activityStack;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        getActivityStack().add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity getCurrentActivity() {
        if (getActivityStack().isEmpty()) {
            return null;
        }
        return getActivityStack().getLast();
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishCurrentActivity() {
        finishAssignActivity(getCurrentActivity());
    }

    /**
     * 结束指定的Activity
     */
    public void finishAssignActivity(Activity activity) {
        if (activity != null) {
            getActivityStack().remove(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        int size = getActivityStack().size() - 1;
        for (int i = size; i >= 0; i--) {
            Activity activity = getActivityStack().get(i);
            if (activity != null && activity.getClass().equals(cls)) {
                Activity activity1 = getActivityStack().remove(i);
                activity1.finish();
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (Activity activity : getActivityStack()) {
            activity.finish();
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
        }
    }

    /**
     * 返回activity堆栈大小
     *
     * @return
     */
    public int getActivityStackSize() {
        return getActivityStack().size();
    }

    /**
     * 是否含有指定activity
     *
     * @param cls
     * @return
     */
    public boolean hasActivity(Class<?> cls) {
        LinkedList<Activity> activityStack = getActivityStack();
        for (Activity activity : activityStack) {
            if (activity != null && cls.equals(activity.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结束所有Activity，除开指定activity
     */
    public void finishAllActivityBesides(Class<?> notIncludedCls) {
        int size = getActivityStack().size() - 1;
        for (int i = size; i >= 0; i--) {
            Activity activity = getActivityStack().remove(i);
            if (activity != null && !activity.getClass().equals(notIncludedCls)) {
                activity.finish();
            }
        }
    }
}