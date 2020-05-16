package com.mobileframe.tools;
/*
 * Copyright (C) 2018

 * 版权所有 tgl
 *
 * 功能描述：第三方sdk库管理，解决debug环境使用，release环境不使用
 *
 *
 * 作者：Created by tgl on 2018/3/27.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */


import android.app.Application;
import android.content.Context;

import com.mobileframe.common.BaseApplication;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ThirdSdkManagement {

    /**
     * 反射方式注册LeakCanary
     *
     * @param application
     */
    public static void initLeakCanary(Application application) {
        initLeakCanaryReflection(application);
    }

    /**
     * 反射方式初始化，只做初始化只能检测activity
     * @param application
     */
    private static void initLeakCanaryReflection(Application application) {
        Method method = getExternalMethod("com.squareup.leakcanary.LeakCanary", "install",
                new Class[]{Application.class});
        if (method != null) {
            try {
                method.invoke(null, application);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static Method getExternalMethod(String classForName, String methodName,
                                           Class<?>[] paramsType) {
        Class<?> threadClazz = null;
        Method method = null;
        try {
            threadClazz = Class.forName(classForName);
            method = threadClazz.getMethod(methodName, paramsType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }
}
