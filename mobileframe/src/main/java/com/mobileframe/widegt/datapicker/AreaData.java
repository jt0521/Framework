package com.mobileframe.widegt.datapicker;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C), 2016-2020,呼我出行网络科技有限公司
 * FileName: AreaData
 * Author: Administrator
 * Date: 2020/8/10 13:34
 * Description: 地区数据
 * History:
 */
public abstract class AreaData implements Serializable {

    public abstract String getName();

    public abstract List<AreaData> child();

}
