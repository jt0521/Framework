package com.mobileframe.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.util.Map;

/**
 * Copyright (C), 2016-2020,呼我出行网络科技有限公司
 * FileName: RequestStoragePermission
 * Author: Administrator
 * Date: 2020/12/1 15:43
 * Description:android 10 请求共享空间存储权限,获得文件夹读写权限；不能获得根文件夹权限
 *
 * @see #getDirPermission(Context, String)  获取缓存权限，如果没有缓存，则直接打开文件管理，询问权限
 * @see #getActivityResult(Context, int, int, Intent, String) 系统回调结果
 * History:
 */
public class RequestStorageDirPermission {

    /**
     * 已获得权限记录文件名
     */
    private static final String PERMISSION_GRANTED = "permission_granted";
    public static final int REQUEST_CODE = 0xa4eb10;

    /**
     * 获取文件夹读写权限
     *
     * @param context
     * @param permissionKey 权限名称，不为空时获取缓存，避免不再次询问
     * @return 未获得权限时返回null
     */
    public static Uri getDirPermission(Context context, String permissionKey) {
        if (TextUtils.isEmpty(permissionKey)) {
            startSafForDirPermission(context);
            return null;
        }
        SharedPreferencesHelper helper = new SharedPreferencesHelper(context, PERMISSION_GRANTED);
        //uriDir 文件夹uri
        String uriDir = helper.getValue(permissionKey);
        if (TextUtils.isEmpty(uriDir)) {
            startSafForDirPermission(context);
        } else {
            try {
                Uri uri = Uri.parse(uriDir);
                final int takeFlags = ((Activity) context).getIntent().getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //获得永久权限，没注销的情况下，不用再次询问
                    context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                }

                return uri;

            } catch (SecurityException e) {
                startSafForDirPermission(context);
            }
        }
        return null;
    }

    /**
     * 获取回调结果
     *
     * @param context
     * @param requestCode
     * @param resultCode
     * @param data
     * @param permissionKey
     * @return 未获得权限时返回null
     */
    public static Uri getActivityResult(Context context, int requestCode, int resultCode, Intent data, String permissionKey) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
            }
            SharedPreferencesHelper helper = new SharedPreferencesHelper(context, PERMISSION_GRANTED);
            helper.putValue(permissionKey, uri.toString());
            return uri;
        }
        return null;
    }

    /**
     * 启动文件夹选择并询问
     *
     * @param context
     */
    public static void startSafForDirPermission(Context context) {
        // 用户可以选择任意文件夹，将它及其子文件夹的读写权限授予APP。
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
        }
    }

    /**
     * 注销所有权限
     *
     * @param context
     */
    public static void releaseAllPermission(Context context) {
        SharedPreferencesHelper helper = new SharedPreferencesHelper(context, PERMISSION_GRANTED);
        Map<String, String> map = (Map<String, String>) helper.getKeyandValue();
        for (String key : map.keySet()) {
            String value = map.get(key);
            releasePermission(context, null, value);
        }
    }

    /**
     * 注销权限
     *
     * @param context
     * @param uri
     * @param permissionKey
     */
    public static void releasePermission(Context context, Uri uri, String permissionKey) {

        SharedPreferencesHelper helper = new SharedPreferencesHelper(context, PERMISSION_GRANTED);
        if (uri == null) {
            String uriStr = helper.getValue(permissionKey);
            if (!TextUtils.isEmpty(uriStr)) {
                uri = Uri.parse(uriStr);
            }
        }
        helper.removeValue(permissionKey);

        if (uri != null) {
            try {
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    context.getContentResolver().releasePersistableUriPermission(uri, takeFlags);
                }
            } catch (SecurityException e) {
                e.fillInStackTrace();
            }
        }
    }
}
