package com.mobileframe.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.core.app.NotificationCompat;

import android.widget.Toast;

import com.mobileframe.tools.SystemUtils;
import com.toast.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * App升级
 */
public class UpdateVersionService extends Service {

    /**
     * 是否下载更新
     */
    public static final String IS_UPDATE_DOWNLOAD = "isUpdateDownload";
    public static final String DOWNLOAD_URL = "download_url";
    public static final String ICON_ID = "icon_id";
    private int iconId = -1;
    /**
     * 保存的文件名
     */
    private String downloadFileName = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getBooleanExtra(IS_UPDATE_DOWNLOAD, false)) {// 是否下载更新
                mIsDisconnect = false;
                iconId = intent.getIntExtra(ICON_ID, -1);
                downloadApk(intent.getStringExtra(DOWNLOAD_URL));
            } else {
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopDownload(null);
        super.onDestroy();
    }

    /**
     * 通知
     */
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    private void downloadApk(String downURL) {
        if (isDownloading) {
            ToastUtils.showToast(getApplicationContext(),"正在下载...");
            return;
        }
        int lastIndex = downURL.lastIndexOf("/");
        downloadFileName = downURL.substring(lastIndex + 1);
        String filePath = null;
        if (android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED)) {
            File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
            File appFilesDir = new File(new File(dataDir, getPackageName()), "files");
            if (!appFilesDir.exists()) {
                appFilesDir.mkdirs();
            }
            filePath = appFilesDir.getPath();
        } else {
            filePath = getFilesDir().getPath();
        }
        File apk_file = new File(filePath, downloadFileName);
        if (apk_file.exists()) {
            apk_file.delete();
        }
        try {
            apk_file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initNotification(apk_file);
        download(downURL, apk_file);
    }

    /**
     * 初始化通知
     */
    private void initNotification(File file) {
        registerMyBroadcast(file);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(this)
                .setTicker("正在下载新版本...")
                .setContentTitle("正在下载安装包...")
                .setContentText("已下载：0.00%(移除通知停止下载)")
                .setContentIntent(contentIntent)
                .setDeleteIntent(
                        PendingIntent.getBroadcast(this, 0, new Intent(this
                                .getClass().getName()), 0));// 为关闭按钮注册广播接收器
        if (iconId != -1) {
            builder.setSmallIcon(iconId);
        }
        notificationManager.notify(225, builder.build());
    }

    /**
     * 注册通知关闭按钮事件广播
     */
    public void registerMyBroadcast(File file) {
        if (myBroadcastReceiver != null) {
            unregisterReceiver(myBroadcastReceiver);
            myBroadcastReceiver = null;
        }
        myBroadcastReceiver = new MyBroadcastReceiver(file);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(this.getClass().getName());
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    /**
     * 关闭下载广播实例
     */
    private MyBroadcastReceiver myBroadcastReceiver = null;

    /**
     * 通知上的关闭按钮（停止下载并关闭）使用的广播接收器
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {
        private File file;

        public MyBroadcastReceiver(File file) {
            this.file = file;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                notificationManager.cancel(225);
                stopDownload(file);
            }
        }
    }

    /**
     * 停止下载的后续操作
     */
    private void stopDownload(File file) {
        mIsDisconnect = true;
        if (notificationManager != null) {
            notificationManager.cancel(225);
            notificationManager = null;
        }
        if (myBroadcastReceiver != null) {
            unregisterReceiver(myBroadcastReceiver);
            myBroadcastReceiver = null;
        }
        if (file != null && file.exists() && file.isFile())
            file.delete();
        stopSelf();
    }

    /**
     * 是否停止下载
     */
    private boolean mIsDisconnect = false;
    private double mProgressCount = -1;

    private void download(final String download_url, final File file) {
        final DecimalFormat df1 = new DecimalFormat("0.00%");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(download_url);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        // 获取文件的大小
                        float size = conn.getContentLength();
                        InputStream is = conn.getInputStream();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int len;
                        int count = 0;
                        while ((len = is.read(buffer)) != -1) {
                            if (mIsDisconnect) {
                                conn.disconnect();
                                mHandler.sendEmptyMessage(3);
                                return;
                            }
                            fos.write(buffer, 0, len);
                            count += len;
                            double progress = (double) count / size;
                            if (progress > 1.0) {
                                updateNotify("100%");
                            } else if (progress - mProgressCount > 0.01) {
                                mProgressCount = progress;
                                updateNotify(df1.format(progress));
                            }
                        }
                        fos.flush();
                        is.close();
                        fos.close();
                        if (file.isFile()) {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = file.getAbsolutePath();
                            mHandler.sendMessage(msg);
                        } else {
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    /**
     * 是否正在下载
     */
    private boolean isDownloading = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                stopSelf();
                mProgressCount = -1;
                ToastUtils.showToast(getApplicationContext(),"下载成功");
                SystemUtils.installApk(getApplicationContext(), new File(msg.obj.toString()));
            } else if (msg.what == 2) {
                isDownloading = true;
            } else if (msg.what == 3) {
                ToastUtils.showToast(getApplicationContext(),"已取消下载");
            } else {
                mProgressCount = -1;
                ToastUtils.showToast(getApplicationContext(),"下载失败");
            }
        }
    };

    private void updateNotify(String progress) {
        if (builder != null && notificationManager != null) {
            builder.setContentText("已下载：" + progress + "(移除通知停止下载)");
            notificationManager.notify(225, builder.build());
        }
    }
}
