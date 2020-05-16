package com.mobileframe.model;

import java.io.Serializable;

/**
 * 实体基类
 *
 * @param <T>
 */

public class BaseBean<T> implements Serializable {

    private boolean success;
    public boolean update;
    public int error;
    public int code;
    public String message;
    public T data;
    public T rows;

    public boolean isSuccess() {
        return code == 0;
    }
}
