/*
 * @(#)DubboGeneralizeBean.java 2018年8月21日 上午9:23:26
 * Copyright 2017 施建波, Inc. All rights reserved. olymtech.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.dubbo;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * <p>File：DubboGeneralizeBean.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2018年8月21日 上午9:23:26</p>
 * <p>Company: olymtech.com</p>
 * @author 施建波
 * @version 1.0
 */
public class DubboGeneralizeBean implements Serializable{

	/** serialVersionUID*/
	    
	private static final long serialVersionUID = 1L;
	
	//节点名
	private String reggroup;
	//组名
	private String group;
	//版本号
	private String version;
	//调用参数
	private List<DubboParamBean> paramList;
	
	public String getReggroup() {
		return reggroup;
	}
	public void setReggroup(String reggroup) {
		this.reggroup = reggroup;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List<DubboParamBean> getParamList() {
		return paramList;
	}
	public void setParamList(List<DubboParamBean> paramList) {
		this.paramList = paramList;
	}
	
//	public static void main(String[] args){
//		DubboGeneralizeBean db = new DubboGeneralizeBean();
//		db.setReggroup("aaaa");
//		db.setGroup("bbbb");
//		db.setVersion("1.0");
//
//		List<DubboParamBean> paramList = Lists.newArrayList();
//
//		DubboParamBean param = new DubboParamBean();
//		param.setParamTyep("java.util.List");
//		param.setParamValue(null);
//		paramList.add(param);
//		DubboParamBean param1 = new DubboParamBean();
//		param1.setParamTyep("java.lang.Integer");
//		param1.setParamValue(1);
//		paramList.add(param1);
//		db.setParamList(paramList);
//
//		System.out.println(JSON.toJSONString(db));
//
//
//	}
}
