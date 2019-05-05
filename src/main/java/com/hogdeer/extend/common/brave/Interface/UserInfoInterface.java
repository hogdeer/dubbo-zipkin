/*
 * @(#)UserInfoInterface.java 2017年10月13日 下午2:57:28
 * Copyright 2017 施建波, Inc. All rights reserved. cargogm.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.brave.Interface;

import com.hogdeer.extend.common.brave.entity.BraveUserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>File：UserInfoInterface.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年10月13日 下午2:57:28</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public interface UserInfoInterface {

	/**
	 * 获取用户信息
	 * @return
	 * @author 施建波  2017年10月13日 下午2:59:39
	 */
	public BraveUserInfo getUserInfo();
	
	/**
	 * 获取用户信息
	 * @param request
	 * @return
	 * @author 施建波  2017年11月29日 下午7:29:30
	 */
	public BraveUserInfo getUserInfo(HttpServletRequest request);  
}
