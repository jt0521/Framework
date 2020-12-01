package com.mobileframe.tools;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.toast.ToastUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/9/6.
 * 启动系统app
 *
 * @see #openFileSelector(Context, String, int)  系统单文件选择器 选择文件
 */
public class SystemUtils {

    /**
     * 启动浏览器
     *
     * @param context
     * @param url
     */
    public static void startBrowser(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    /**
     * 安装APK
     *
     * @param file
     */
    public static void installApk(Context context, File file) {
        if (file == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判读版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//判断是否是8.0或以上
                boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                if (!haveInstallPermission) {
                    Uri packageURI = Uri.parse("package:" + context.getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    context.startActivity(install);
                }
            } else {
                Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                context.startActivity(install);
            }
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        }
    }

    public static String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    /**
     * 拨号界面
     *
     * @param context
     * @param phoneNum 电话号码
     */
    public static void startCallNum(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 拨号中
     *
     * @param context
     * @param phoneNum
     */
    public static void startCalling(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ToastUtils.showToast(context, "请开启拨号权限");
            return;
        }
        context.startActivity(intent);
    }

    /**
     * 发送短信
     *
     * @param context
     * @param phoneNum   接收短信号码
     * @param msgContent 发送内容
     */
    public static void sendMessage(Context context, String phoneNum, String msgContent) {
        Intent intent = new Intent("android.intent.action.SENDTO",
                Uri.parse("smsto:" + phoneNum));
        intent.putExtra("sms_body", msgContent); // 默认短信文字
        context.startActivity(intent);
    }

    /**
     * 开启位置服务页面
     *
     * @param context
     */
    public static void openLocationServiceSetting(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 打开设置页面
     *
     * @param context
     */
    public static void openSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", PkgUtil.getPackageName(context), null));
        context.startActivity(intent);
    }

    /**
     * 打开系统文件管理器
     * android10 及以上必须使用系统管理器访问共享文件
     *
     * @param context
     * @param mineType    过滤文件类型
     * @param requestCode
     */
    public static void openFileSelector(Context context, String mineType, int requestCode) {
        //通过系统的文件浏览器选择一个文件
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //筛选，只显示可以“打开”的结果，如文件(而不是联系人或时区列表)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //过滤只显示图像类型文件
        intent.setType(mineType);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
    }
}
