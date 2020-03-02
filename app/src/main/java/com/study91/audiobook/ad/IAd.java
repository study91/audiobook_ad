package com.study91.audiobook.ad;

import android.view.View;

/**
 * 广告接口
 */
public interface IAd {
	/**
	 * 获取Banner广告视图
     */
	View getBannerView();

	/**
	 * 释放资源
	 */
	void release();
}
