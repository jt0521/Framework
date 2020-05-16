package com.mobileframe.tools;

import com.alibaba.fastjson.TypeReference;

/*
 *

 * 版权所有
 *
 * 功能描述：封装json type,避免直接使用第三方解析库
 *
 *
 * 作者：Created by tgl on 2018/6/4.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
// google json extends TypeToken
// fast json extends TypeReference
public class JsonType<T> extends TypeReference {
}
