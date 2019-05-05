/*
 * @(#)CusBraveServletFilter.java 2017年11月29日 下午6:42:40
 * Copyright 2017 施建波, Inc. All rights reserved. cargogm.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.brave.filter;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ServerRequestInterceptor;
import com.github.kristofa.brave.ServerResponseInterceptor;
import com.github.kristofa.brave.ServerSpanThreadBinder;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.HttpResponse;
import com.github.kristofa.brave.http.HttpServerResponseAdapter;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.hogdeer.extend.common.bean.DubboPublishBean;
import com.hogdeer.extend.common.brave.Interface.UserInfoInterface;
import com.hogdeer.extend.common.brave.entity.BraveUserInfo;
import com.hogdeer.extend.common.brave.springmvc.CusHttpServerRequestAdapter;
import com.hogdeer.extend.common.consts.ApplicationConst;
import com.hogdeer.extend.common.dubbo.PublishDesider;
import com.hogdeer.extend.common.utils.IPUtils;
import com.hogdeer.extend.common.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>File：CusBraveServletFilter.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年11月29日 下午6:42:40</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class CusBraveServletFilter implements Filter {
	
	private final static Logger logger = LoggerFactory.getLogger(CusBraveServletFilter.class);
	
	private final Brave brave;
    private final ServerRequestInterceptor requestInterceptor;
    private final ServerResponseInterceptor responseInterceptor;
    private final ServerSpanThreadBinder serverThreadBinder;
    private final SpanNameProvider spanNameProvider;
    private UserInfoInterface userInfoInterface;
	private ServerSpanThreadBinder serverSpanThreadBinder;

	CusBraveServletFilter() {
    	this.brave = null;
        this.requestInterceptor = null;
        this.responseInterceptor = null;
		this.serverThreadBinder = null;
        this.spanNameProvider = new DefaultSpanNameProvider();
    }
    
    CusBraveServletFilter(Brave brave, UserInfoInterface userInfoInterface) {
    	this.brave = brave;
        this.requestInterceptor = brave.serverRequestInterceptor();
        this.responseInterceptor = brave.serverResponseInterceptor();
        this.serverThreadBinder = brave.serverSpanThreadBinder();
        this.spanNameProvider = new DefaultSpanNameProvider();
        this.userInfoInterface = userInfoInterface;
    }
    
	public void setUserInfoInter(UserInfoInterface userInfoInter) {
		this.userInfoInterface = userInfoInter;
	}


	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException { 
		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		final HttpServletResponse servletResponse = (HttpServletResponse) response;
		String url = servletRequest.getRequestURI();
		try{
			DubboPublishBean dubboPublishBean = new DubboPublishBean();
	    	dubboPublishBean.setIp(IPUtils.getOriginalIpAddr(servletRequest)); 
	    	String data = this.getData(servletRequest);
			BraveUserInfo braveUserInfo = null;
			if(null != this.userInfoInterface){
	    		braveUserInfo = this.userInfoInterface.getUserInfo(servletRequest); 
	    		if(null != braveUserInfo){
	    			dubboPublishBean.setUsername(braveUserInfo.getUserName());
	    	    	dubboPublishBean.setOrgId(braveUserInfo.getOrgId());  
	    		}
	    	}
			RpcContext.getContext().setAttachment(PublishDesider.PUBLISH_FILTER_KEY, JSON.toJSONString(dubboPublishBean));
			if(isBrave(url)){
				CusHttpServerRequestAdapter csRequest = new CusHttpServerRequestAdapter(servletRequest,data, braveUserInfo, spanNameProvider);
		    	requestInterceptor.handle(csRequest);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e); 
		}
    	try{
    		chain.doFilter(request, response);
    	}finally {
    		if(isBrave(url)){
	    		try{
	    		       responseInterceptor.handle(new HttpServerResponseAdapter(new HttpResponse() { 
	    		           public int getHttpStatusCode() {
	    		               return servletResponse.getStatus();
	    		           }
	    		       }));
	    	    }catch(Exception e){
	    	    	   logger.error(e.getMessage(), e); 
	    	    }
    		}
    	}
	}
	
	private String getData(final HttpServletRequest request){
    	String data = StringUtils.EMPTY;
    	JSONObject json = new JSONObject();
    	Object obj = request.getAttribute(ApplicationConst.REQUEST_BODY_PARAM_JSON);
    	if(!ValidateUtils.isObjectNull(obj)){
    		json.put("requestBody", obj);
    	}
    	data = JSON.toJSONString(request.getParameterMap());
    	if(StringUtils.isNotBlank(data)){
    		json.put("form", data);
    	}
    	data = request.getQueryString();
    	if(StringUtils.isNotBlank(data)){
    		json.put("query", data);
    	}
    	return json.toJSONString();
    }

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}
	
	private Boolean isBrave(String url){
		return (null != brave && (url.indexOf("applogin") != -1 || url.lastIndexOf("quickSearchControll.jsp") !=-1));
	}

}
