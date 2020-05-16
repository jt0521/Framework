package com.mobileframe.tools;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 * 作者：tgl on 2016/11/16 15:06
 * Description：将application中的第三方sdk初始化异步处理
 * 修改时间：
 * 修改内容：
 */
public abstract class InitializeService extends IntentService {

    private static final String ACTION_INIT_WHEN_APP_CREATE = "com.app.service.action.INIT";
    //是否已经初始化
    private static boolean hasInit;

    public InitializeService() {
        super("InitializeService");
    }

    public static void startService(Context context, Class serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        intent.setAction(InitializeService.ACTION_INIT_WHEN_APP_CREATE);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT_WHEN_APP_CREATE.equals(action)) {
                if (!hasInit){
                    hasInit = true;
                    performInit(getApplicationContext());
                }
            }
        }
    }

    /**
     * application onCreate data
     * 初始化数据
     */
    public abstract void performInit(Context context);
}
