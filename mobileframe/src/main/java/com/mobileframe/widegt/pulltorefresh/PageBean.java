package com.mobileframe.widegt.pulltorefresh;

import java.io.Serializable;

/**
 * Copyright (C), 2020, nqyw
 * FileName: tgl
 * Author: 10496
 * Date: 2020/3/16 17:44
 * Description: 分页参数
 * History:
 */
public class PageBean<T> implements Serializable {
    //页码
    public int pageNo;
    //每页页数
    public int perPageSize;
    /**
     * 总条数
     */
    public int total;
    public int pageSize;
    /**
     * 是否有上一页
     */
    public boolean hasPrevious;
    /**
     * 是否有下一页
     */
    public boolean hasNext;
    //上页页码
    public int previousPage;
    /**
     * 下页页码
     */
    public int nextPage;
    public T rows;
}
