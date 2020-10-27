package com.mobileframe.tools;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.mobileframe.common.ActivityStackManager;
import com.toast.ToastUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * @description 未捕获异常处理类
 * @data: 2015年7月14日 上午10:16:17
 */
public class CrashExceptionHandler implements UncaughtExceptionHandler {
    private static final CrashExceptionHandler INSTANCE = new CrashExceptionHandler();
    private UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private String phoneType;

    /**
     * 私有构造
     */
    private CrashExceptionHandler() {
    }

    /**
     * 获取对象实例
     *
     * @return
     */
    public static CrashExceptionHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        this.mContext = context;

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (null != mDefaultHandler && !handlerException(ex)) {
            mDefaultHandler.uncaughtException(thread, ex);// 系统默认处理
            System.out.println("uncaughtException---------------System default");
        } else {
            try {
                Thread.sleep(3000);// 400可以写入文件huaweiG520T10
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("uncaughtException---------------System.exit(1)");

            ActivityStackManager.getInstance().finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 处理异常方法
     *
     * @param
     * @return true:表示处理;false:未处理
     */
    private boolean handlerException(final Throwable ex) {
        if (null == ex)
            return false;

        Log.e(mContext.getPackageName(), Log.getStackTraceString(ex));
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                ToastUtils.showToast(mContext,"程序异常退出");
                saveLog2File(ex);
                Looper.loop();
            }
        }).start();

        return true;
    }

    /**
     * 保存日志信息
     *
     * @param ex
     */
    private String saveLog2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        String dir = FileUtils.getDiskDir(mContext, "Log");
        String time = DateFormat.format("yyyyMMdd_kkmmss", new Date()).toString();

        File filePath = new File(dir, time + ".txt");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        collectDeviceInfo(pw);
        ex.printStackTrace(pw);
        pw.close();
        sb.append(sw.toString());

        //		Intent service = new Intent(mContext, UploadLogService.class);
        //		service.putExtra("loginfo", sw.toString());
        //		service.putExtra("phoneType", this.phoneType);
        //		mContext.startService(service);

        FileWriter fr = null;
        try {
            fr = new FileWriter(filePath);
            fr.write(sw.toString());
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();

            if (null != fr) {
                try {
                    fr.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    fr = null;
                }
            }
        }

        return sw.toString();
    }

    /**
     * 收集设备信息
     * <p>
     * 在frameworks/base/core/java/android/os/Build.java中定义了class
     * Build类，这个类定义了所有关于产品的参数，例如固件版本号，product 名字，板子名字等等，有些参数会在设置->关于手机中显示。
     * 因此，如果想修改这些参数，只要在修改相关的参数就可以了。例如，如果想修改固件版本号，那么找到定义ro.product.firmware的文件（
     * 在我做的项目里，是./device/softwinner/crane-gm-g9/crane_gm_g9.mk），修改成想要的版本号，就可以了。
     * <p>
     * 要注意的是，在重新编译的时候，首先要先删掉./out/target/product/crane-gm-g9/recovery/root/
     * default
     * .prop和./out/target/product/crane-gm-g9/system/build.prop再编译才能生效。否则，
     * 这个build不一定能重新编译，因为改的是mk文件本身，而不是.java。
     * <p>
     */
    private void collectDeviceInfo(PrintWriter sb) {
        String newLine = System.getProperty("line.separator");
        sb.append("日志时间:" + DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date()));
        sb.append(newLine);

        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                /*if (field.getName().equals("DEVICE")) {
                    this.phoneType = field.get(null).toString();
                }*/

                field.setAccessible(true);
                String str = field.getName() + " = " + field.get(null).toString() + newLine;
                sb.append(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
