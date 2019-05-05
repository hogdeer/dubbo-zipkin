/*
 * @(#)DubboParamBean.java 2018年8月21日 上午9:30:17
 * Copyright 2017 施建波, Inc. All rights reserved. olymtech.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.dubbo;

import java.io.Serializable;

/**
 * <p>File：DubboParamBean.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2018年8月21日 上午9:30:17</p>
 * <p>Company: olymtech.com</p>
 * @author 施建波
 * @version 1.0
 */
public class DubboParamBean implements Serializable{

	/** serialVersionUID*/
	    
	private static final long serialVersionUID = 1L;
	
	private String paramTyep;
	private Object paramValue;
	
	public String getParamTyep() {
		return paramTyep;
	}
	public void setParamTyep(String paramTyep) {
		this.paramTyep = paramTyep;
	}
	public Object getParamValue() {
		return paramValue;
	}
	public void setParamValue(Object paramValue) {
		this.paramValue = paramValue;
	}
}
