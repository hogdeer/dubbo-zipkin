package com.hogdeer.extend.common.brave.entity;

import java.io.Serializable;

public class BraveUserInfo implements Serializable {

	//
	private static final long serialVersionUID = 64668552759629432L;
	
	private String userId;		//用户编号
	private String userName;	//用户名	
	private String orgId;		//公司编号
	private String orgName;		//公司名称
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	
	
}
