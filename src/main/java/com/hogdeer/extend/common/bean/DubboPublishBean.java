/*
 * @(#)DubboPublishBean.java 2017年11月23日 下午4:30:16
 * Copyright 2017 施建波, Inc. All rights reserved. cargogm.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.bean;

import java.io.Serializable;

/**
 * <p>File：DubboPublishBean.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年11月23日 下午4:30:16</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class DubboPublishBean implements Serializable{

	//
	private static final long serialVersionUID = 4420295288481420747L;
	
	private String ip;			//IP
	private String username;	//用户名
	private String orgId;			//公司编号
	private String cookieValue;		//COOKIE
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getCookieValue() {
		return cookieValue;
	}
	public void setCookieValue(String cookieValue) {
		this.cookieValue = cookieValue;
	}
}
