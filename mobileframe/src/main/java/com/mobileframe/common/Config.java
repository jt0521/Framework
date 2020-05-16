package com.mobileframe.common;
/*
 *
 * 功能描述：配置管理，环境配置、区分
 *
 *
 * 作者：Created by tgl on 2018/6/20.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */

import com.mobileframe.BuildConfig;

public class Config {

    /**
     * 是否测试环境
     */
    public static boolean isDebugEniv;

    static {
        isDebugEniv = checkEniv();
    }

    private static boolean checkEniv() {
        if (BuildConfig.ENVI_TYPE < 100) {
            return true;
        }
        return false;
    }
}
