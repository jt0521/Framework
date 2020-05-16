package com.net.netretrofit.callback;

import java.io.Serializable;

/**
 * @author tgl
 * 接口通用数据格式
 */
public class ResultBean<T> implements Serializable {
    /**
     * 状态码
     */
    public int code;
    /**
     * 一般用于错误提示
     */
    public String msg;
    /**
     * 数据
     */
    public T body;
}
