package com.mobileframe.widegt.pulltorefresh;


/**
 *
 * 功能描述：
 * 创建日期：2017-10-6
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AbsSwipeCacheRequestParams {
	public String url; //http网络请求url
	
	public String cachePath; //本地缓存路径
	
	public AbsSwipeCacheAdapter.OnDataLoadListener dataLoadListener;
}
