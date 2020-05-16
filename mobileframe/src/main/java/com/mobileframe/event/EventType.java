package com.mobileframe.event;

import java.io.Serializable;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * Author: 10496
 * Date: 2020/4/7 15:19
 * Description: 消息传递
 * History:
 */
public class EventType implements Serializable {
    /**
     * 定位
     */
    public static final int TYPE_REQUEST_LOCATION = 1;
    public int type;

    public EventType(int type) {
        this.type = type;
    }
}
