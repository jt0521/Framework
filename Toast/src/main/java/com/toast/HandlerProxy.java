package com.toast;

import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * Copyright (C), 2016-2020,呼我出行网络科技有限公司
 * FileName: HandlerProxy
 * Author: Administrator
 * Date: 2020/10/27 16:28
 * Description:解决7.1.1toast报错,将自定义类设给toast，在自定义类中将事件转传Toast内部原handler处理
 * 复现方法:toast show 后，ui线程执行耗时操作达3000秒以上
 * History:
 */
public class HandlerProxy extends Handler {

    private Handler mHandler;

    public HandlerProxy(Handler handler) {
        //此入参handler是toast内部handler对象
        mHandler = handler;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        try {
            //这里的任务就是异常抛出点，因此将它try catch
            mHandler.handleMessage(msg);
        } catch (WindowManager.BadTokenException e) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
        }
    }
}
