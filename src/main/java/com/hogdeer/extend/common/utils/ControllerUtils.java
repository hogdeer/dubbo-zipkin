/*
 * @(#)ControllerUtils.java 2018年6月6日 上午9:30:54
 * Copyright 2017 施建波, Inc. All rights reserved. olymtech.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>File：ControllerUtils.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2018年6月6日 上午9:30:54</p>
 * <p>Company: olymtech.com</p>
 * @author 施建波
 * @version 1.0
 */
public class ControllerUtils {

	public static String getCookieValue(HttpServletRequest request, String name){
		if(StringUtils.isNotBlank(name)){
			Cookie[] cookies = request.getCookies();
			if(ArrayUtils.isNotEmpty(cookies)){
				for(Cookie cookie: cookies){
		            if(name.equals(cookie.getName())){
		            	return cookie.getValue();
		            }
		        }
			}
		}
        return null;
	}
}
