/*
 * @(#)CusServiceAspect.java 2018年3月26日 下午3:44:25
 * Copyright 2018 施建波, Inc. All rights reserved. cargogm.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.brave.service;

import com.alibaba.fastjson.JSON;
import com.github.kristofa.brave.*;
import com.google.common.collect.Maps;
import com.hogdeer.extend.common.bean.DiamondConfig;
import com.hogdeer.extend.common.utils.ArrayHandleUtils;
import com.hogdeer.extend.common.utils.IPUtils;
import com.hogdeer.extend.common.utils.StringHandleUtils;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zipkin.Constants;

import java.util.List;
import java.util.Map;

/**
 * <p>File：CusServiceAspect.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2018年3月26日 下午3:44:25</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class CusServiceAspect {

	private static final Logger logger = LoggerFactory.getLogger(CusServiceAspect.class);
	
	private String serviceSpanName = "service";
	
	private Brave brave;
	private ClientTracer clientTracer;
	private ServerTracer serverTracer;
	private ClientSpanThreadBinder clientSpanThreadBinder;
	private ServerSpanThreadBinder serverThreadBinder;
	
	private DiamondConfig braveConfig;
	
	private Integer ipInt;

	public void setBrave(Brave brave) {
		this.brave = brave;
		this.clientTracer = brave.clientTracer();
		this.serverTracer = brave.serverTracer();
		this.clientSpanThreadBinder = brave.clientSpanThreadBinder();
		this.serverThreadBinder = brave.serverSpanThreadBinder();
	}

	public void setBraveConfig(DiamondConfig braveConfig) {
		this.braveConfig = braveConfig;
	}

	public Object around(ProceedingJoinPoint jointPoint) throws Throwable{
		//Map<String, Object> spanMap = Maps.newHashMap();
		/*try{
			spanMap = this.beginTrace(jointPoint);
			Object result = jointPoint.proceed();
			this.endTrace(spanMap, null, result);
			return result;
		}catch(Exception e){
			this.endTrace(spanMap, e, null);
			throw e;
		}*/
		Map<String, Object> spanMap = this.beginTrace(jointPoint);
		Object result = null;
		try{
			result = jointPoint.proceed();
			return result;
		}catch(Exception e){
			throw e;
		}finally{
			this.endTrace(spanMap, null, result);
		}
	}
	
	private Map<String, Object> beginTrace(final ProceedingJoinPoint jointPoint){
		Map<String, Object> spanMap = Maps.newHashMap();
		try{
			if(this.isOpenService()){
				ServerSpan parentSpan = this.serverThreadBinder.getCurrentServerSpan();
				String methodName = jointPoint.getSignature().getName();
				String className = jointPoint.getSignature().getDeclaringType().getSimpleName();
				String params = null;
				Object[] args = jointPoint.getArgs();
				if(ArrayUtils.isNotEmpty(args)){
					params = JSON.toJSONString(jointPoint.getArgs());
				}
				SpanId spanId = this.clientTracer.startNewSpan(StringHandleUtils.connectMulString(".", className, methodName));
		        if(StringUtils.isNotBlank(params)){
		        	this.clientTracer.submitBinaryAnnotation("service.params", params);
		        }
		        try {
		        	this.setClientSent(this.clientTracer, parentSpan);
		        }catch(Exception e){
		        	this.clientTracer.setClientSent();
		        }
		        Span span = this.clientSpanThreadBinder.getCurrentClientSpan();
				this.serverTracer.setStateCurrentTrace(spanId, this.serviceSpanName);
				spanMap.put("span", span);
				spanMap.put("parentSpan", parentSpan); 
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return spanMap;
    }
	
	private void setClientSent(ClientTracer tracer, ServerSpan parentSpan) throws Exception {
		//int ip = IPUtils.ipToInt(IPUtils.getLocalIp());
		/*if(null != parentSpan){
			List<Annotation> annotationList = parentSpan.getSpan().getAnnotations();
			if(CollectionUtils.isNotEmpty(annotationList)){
				ipInt = annotationList.get(0).host.ipv4;
			}
		}*/
		if(null == ipInt){
			ipInt = IPUtils.ipToInt(IPUtils.getLocalIp());
		}
        tracer.setClientSent(Endpoint.builder().ipv4(ipInt).serviceName(this.serviceSpanName).build());
    }
	
	private void endTrace(final Map<String, Object> spanMap, final Exception exception, Object result){
		try{
			if(this.isOpenService() && MapUtils.isNotEmpty(spanMap)){
				try{
					this.clientSpanThreadBinder.setCurrentSpan((Span) spanMap.get("span"));
					this.serverThreadBinder.setCurrentSpan((ServerSpan) spanMap.get("parentSpan"));
					if(null != exception){
						String errMsg = ExceptionUtils.getStackTrace(exception);
			            this.clientTracer.submitBinaryAnnotation(Constants.ERROR, "1");
						this.clientTracer.submitBinaryAnnotation("error.msg", errMsg);
					}
					if(null != result){
						Object tempResult = result;
						List<Object> copyList = ArrayHandleUtils.copyList(result, null);
						if(CollectionUtils.isNotEmpty(copyList)){
							tempResult = copyList;
						}
						/*long sizeOf = RamUsageEstimator.sizeOf(tempResult);
	                	if(sizeOf < 30000L){
	                		this.clientTracer.submitBinaryAnnotation("service.result", JSON.toJSONString(tempResult));
	                	}*/
						String jsonStr = JSON.toJSONString(tempResult);
						if(jsonStr.length()<2000){
							this.clientTracer.submitBinaryAnnotation("service.result", jsonStr);
						}
					}	
				}catch(Exception e){
					throw e;
				}finally{
		        	this.clientTracer.setClientReceived();  
		        }
			}
        }catch(Exception e){
			logger.error(e.getMessage(), e);
        }
    }
	
	private Boolean isOpenService(){
		Boolean isOpenService = Boolean.TRUE;
		if(null != this.braveConfig){
			Object obj = this.braveConfig.getConfigMap().get("brave.isOpenService");
			if(null != obj){
				isOpenService = Boolean.valueOf(obj.toString());
			}
		}
		return isOpenService;
	}
}
