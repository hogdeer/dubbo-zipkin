/*
 * @(#)BraveUtil.java 2018年10月18日 下午5:07:41
 * Copyright 2017 施建波, Inc. All rights reserved. olymtech.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.brave.utils;

import com.github.kristofa.brave.KeyValueAnnotation;
import com.hogdeer.extend.common.bean.DiamondConfig;
import com.hogdeer.extend.common.consts.ApplicationConst;
import com.hogdeer.extend.common.exception.BusinessException;
import com.hogdeer.extend.common.utils.SpringContextUtils;
//import com.olymtech.shopkeeper.common.bean.SpringContextUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * <p>File：BraveUtil.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2018年10月18日 下午5:07:41</p>
 * <p>Company: olymtech.com</p>
 * @author 施建波
 * @version 1.0
 */
public class BraveUtils {
	
	public static String PROJECT_TYPE_NAME = "project.type";
	public static String EXCEPTION_TYPE_NAME = "exception.type";

	public static String getProjectType(){
		String projectType = null;
		if(null != SpringContextUtils.getApplicationContext()){
			if(SpringContextUtils.containsBean("braveConfig")){
				DiamondConfig braveConfig = (DiamondConfig) SpringContextUtils.getBean("braveConfig");
				Object obj = braveConfig.getConfigMap().get(PROJECT_TYPE_NAME);
				if(null != obj){
					projectType = obj.toString();
				}
			}
		}
		return projectType;
	}
	
	/**
	 * 添加项目工程类型名称
	 * @Title: setProjectTypeAnnotation   
	 * @param kas 
	 * void
	 * @author 施建波 2018年10月18日 下午5:25:39
	 */
	public static void setProjectTypeAnnotation(List<KeyValueAnnotation> kas, String annotationName){
		if(null != kas){
			String projectType = getProjectType();
			if(StringUtils.isNotBlank(projectType)){
				KeyValueAnnotation projectTypeAnnotation = KeyValueAnnotation.create(annotationName, projectType);
				kas.add(projectTypeAnnotation);
			}
		}
	}
	
	/**
	 * 添加项目工程异常类型
	 * @Title: setExceptionTypeAnnotaion   
	 * @param kas
	 * @param ex 
	 * void
	 * @author 施建波 2018年10月25日 上午9:59:08
	 */
	public static void setExceptionTypeAnnotaion(List<KeyValueAnnotation> kas, Throwable throwable){
		if(null != kas && null != throwable){
			String exceptionType = getExceptionType(throwable);
			KeyValueAnnotation exceptionTypeAnnotation = KeyValueAnnotation.create(EXCEPTION_TYPE_NAME, exceptionType);
			kas.add(exceptionTypeAnnotation);
		}
	}
	
	/**
	 * 获取异常类型
	 * @Title: getExceptionType   
	 * @param throwable
	 * @return 
	 * String
	 * @author 施建波 2018年10月25日 上午10:22:00
	 */
	public static String getExceptionType(Throwable throwable){
		String exceptionName = throwable.getClass().getSimpleName();
		//系统异常：0	业务异常：1
		String exceptionType = "0";
		if(exceptionName.equalsIgnoreCase("BusinessException")){
			exceptionType = "1";
			if(throwable instanceof BusinessException){
				BusinessException businessException = (BusinessException) throwable;
				if(businessException.getCode().compareTo(ApplicationConst.ERROR_CODE_SYSTEM_FAILUE) == 0){
					exceptionType = "0";
				}
			}
		} 
		return exceptionType;
	}
}
