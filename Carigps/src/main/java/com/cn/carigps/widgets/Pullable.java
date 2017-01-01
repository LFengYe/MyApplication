package com.cn.carigps.widgets;

public interface Pullable {
	/**
	 * 判断是否可以下拉，如果不要下拉功能可以直接return false
	 *
	 * @return true如果可以下拉否则返回false
	 */
	boolean canPullDown();
}