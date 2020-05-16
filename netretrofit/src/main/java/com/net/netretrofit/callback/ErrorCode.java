package com.net.netretrofit.callback;

/**
 * @author tgl
 * 错误码定义，与后端约定一致
 */
public class ErrorCode {
    //================================LOCAL <0====================================
    /**
     * HTTP 不确定
     */
    public static final int HTTP_UNSPECIFIC = -10000;
    /**
     * HTTP失败
     */
    public static final int HTTP_EX = -1;
    /**
     * 类型转换失败
     */
    public static final int CAST_EX = -2;
    /**
     * 下载失败
     */
    public final static int FILE_DOWNLOAD_FAIL = -3;
    /**
     * 上传失败
     */
    public final static int FILE_UPLOAD_FAIL = -4;


    //================================SUCCESS 200=================================
    /**
     * 成功
     */
    public final static int SUCCESS = 200;


    //===============================ERROR 4xx====================================
    /**
     * 参数无效
     */
    public final static int PARAMS_INVALID = 400;

    /**
     * 未登录
     */
    public final static int NOT_LOGIN = 401;

    /**
     * 未授权
     */
    public final static int UNAUTHORIZED = 402;

    /**
     * Token无效
     */
    public final static int TOKEN_INVALID = 403;

    /**
     * 非法访问
     */
    public final static int ILLEGAL_ACCESS = 404;

    /**
     * 系统升级维护
     */
    public final static int SYSTEM_UPGRADE_MAINTENANCE = 405;

    /**
     * 业务调整
     */
    public final static int BUSINESS_ADJUSTMENT = 406;

    /**
     * APP需要升级
     */
    public final static int APP_UPGRADE = 407;

    /**
     * 请求时间太短（太频繁）
     */
    public final static int APP_REQUEST_FREQUENTLY = 408;


    //===============================ERROR 5xx====================================

}
