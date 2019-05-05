/*
 * @(#)ZipkinDailyRollingFileAppender.java 2018年7月30日 上午9:28:25
 * Copyright 2017 施建波, Inc. All rights reserved. olymtech.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.brave.log;

import com.alibaba.fastjson.JSONObject;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ClientTracer;
import com.github.kristofa.brave.SpanId;
import com.hogdeer.extend.common.bean.DiamondConfig;
import com.hogdeer.extend.common.utils.IPUtils;
import com.hogdeer.extend.common.utils.SpringContextUtils;
import com.twitter.zipkin.gen.Endpoint;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>File：ZipkinDailyRollingFileAppender.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2018年7月30日 上午9:28:25</p>
 * <p>Company: olymtech.com</p>
 * @author 施建波
 * @version 1.0
 */
public class Log4jModelhomeDailyRollingFileAppender extends DailyRollingFileAppender {
	
	private final static Logger logger = LoggerFactory.getLogger(Log4jModelhomeDailyRollingFileAppender.class);
	
	private static volatile Brave brave;
    private static volatile ClientTracer clientTracer;
    private DiamondConfig braveConfig;
    
    private Integer ipInt;
	
	public Log4jModelhomeDailyRollingFileAppender(){
		try{
			String braveBeanStr = "brave";
			if(SpringContextUtils.containsBean(braveBeanStr)){
				Object obj = SpringContextUtils.getBean(braveBeanStr);
				this.brave = (Brave) obj;
				this.clientTracer = brave.clientTracer();
				this.braveConfig = (DiamondConfig) SpringContextUtils.getBean("braveConfig");
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	protected void subAppend(LoggingEvent event) {
		super.subAppend(event);
		Object msgObj = event.getMessage(); 
		if(msgObj instanceof JSONObject){
			JSONObject json = (JSONObject) msgObj;
			if(json.getBoolean("isZipkin")){
				this.beginTrace(event);
			}
		}
	}
	
	private void beginTrace(LoggingEvent event){
		try{
			if(this.isOpenLog() && null != this.clientTracer){
				Level level = event.getLevel();
				if(("info".equalsIgnoreCase(level.toString()) || "error".equalsIgnoreCase(level.toString()))){
					try{
						LocationInfo locationInfo = event.getLocationInformation();
						String requestName = locationInfo.fullInfo;
						if(StringUtils.isNotBlank(requestName)){
							int offsize = requestName.indexOf("(");
							if(offsize >= 0){
								requestName = requestName.substring(0, offsize);
							}
						}else{
							requestName = level.toString();
						}
						SpanId spanId = this.clientTracer.startNewSpan(requestName); 
						//this.clientTracer.submitBinaryAnnotation("log.level", level.toString());
						//this.clientTracer.submitBinaryAnnotation("log.message", (String) event.getMessage());
						this.setClientSent(event.categoryName);
					}catch(Exception e){
						logger.error(e.getMessage(), e);
					}finally{
						this.clientTracer.setClientReceived();  
			        }
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
    }
	
	private Boolean isOpenLog(){
		Boolean isOpenSql = Boolean.TRUE;
		if(null != this.braveConfig){
			Object obj = this.braveConfig.getConfigMap().get("brave.isOpenLog");
			if(null != obj){
				isOpenSql = Boolean.valueOf(obj.toString());
			}
		}
		return isOpenSql;
	}
	
	private void setClientSent(String className) throws Exception {
		if(null == this.ipInt){
			this.ipInt = IPUtils.ipToInt(IPUtils.getLocalIp());
		}
		if(StringUtils.isBlank(className)){
			className = "service";
		}else{
			int offsize = className.lastIndexOf(".");
			if(offsize >= 0){
				className = className.substring(offsize + 1);
			}
		}
        this.clientTracer.setClientSent(Endpoint.builder().ipv4(this.ipInt).serviceName(className).build());
    }
}
