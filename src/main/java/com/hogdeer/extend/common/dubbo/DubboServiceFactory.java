/*
 * @(#)DubboServiceFactory.java 2018年8月20日 下午6:50:13
 * Copyright 2017 施建波, Inc. All rights reserved. olymtech.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hogdeer.extend.common.utils.DubboUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * <p>File：DubboServiceFactory.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2018年8月20日 下午6:50:13</p>
 * <p>Company: olymtech.com</p>
 * @author 施建波
 * @version 1.0
 */
public class DubboServiceFactory {
	
	private Logger logger = Logger.getLogger(DubboServiceFactory.class);

	private ApplicationConfig application;
    private RegistryConfig registry;
    
    private String applicationName;
    private String registryAddress;
    private String reggroup;
    
    @SuppressWarnings("rawtypes")
	private Map<String, ReferenceConfig> referenceCache = Maps.newHashMap();
    
    public void init(){
    	ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(this.applicationName); 
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(this.registryAddress); 
        this.application = applicationConfig;
        this.registry = registryConfig;
    }
    
    public Object genericInvoke(String interfaceClass, String methodName, DubboGeneralizeBean dubboGeneralizeBean){
    	
    	List<String> paramTyepList = Lists.newArrayList();
    	List<Object> paramList = Lists.newArrayList();
    	String tempReggroup = (StringUtils.isNotBlank(this.reggroup)) ? this.reggroup:"dubbo";
    	String group = null;
    	String version = null;
    	if(null != dubboGeneralizeBean){
    		if(StringUtils.isNotBlank(dubboGeneralizeBean.getReggroup())){
    			tempReggroup = dubboGeneralizeBean.getReggroup();
    		}
    		group = dubboGeneralizeBean.getGroup();
    		version = dubboGeneralizeBean.getVersion();
    		List<DubboParamBean> dubboParamList = dubboGeneralizeBean.getParamList();
    		if(CollectionUtils.isNotEmpty(dubboParamList)){
    			for(DubboParamBean item:dubboParamList){
    				String paramTyep = item.getParamTyep();
    				Object valObj = item.getParamValue();
    				paramTyepList.add(paramTyep);
    				paramList.add(valObj);
    			}
    		}
    	}
    	ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
    	String cacheKey = DubboUtils.getReferenceCacheKey(tempReggroup, group, interfaceClass, version);
    	if(this.referenceCache.containsKey(cacheKey)){
    		reference = this.referenceCache.get(cacheKey);
    	}else{
    		reference.setApplication(this.application); 
	        reference.setRegistry(this.registry); 
	        reference.setInterface(interfaceClass);
	        reference.setGeneric(true);
	        reference.setCheck(Boolean.FALSE);
	        reference.setReggroup(tempReggroup);
	        reference.setVersion(version);
	        reference.setGroup(group);
	        this.referenceCache.put(cacheKey, reference);
    	}

        GenericService genericService = reference.get(); 
        logger.info("调用DUBBO接口=========="+interfaceClass+":"+methodName+",dubboGeneralizeBean:"+JSON.toJSONString(dubboGeneralizeBean));
        return genericService.$invoke(methodName, paramTyepList.toArray(new String[]{}), paramList.toArray());
    }
    
    
	public ApplicationConfig getApplication() {
		return application;
	}
	public void setApplication(ApplicationConfig application) {
		this.application = application;
	}
	public RegistryConfig getRegistry() {
		return registry;
	}
	public void setRegistry(RegistryConfig registry) {
		this.registry = registry;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getRegistryAddress() {
		return registryAddress;
	}
	public void setRegistryAddress(String registryAddress) {
		this.registryAddress = registryAddress;
	}

	public String getReggroup() {
		return reggroup;
	}

	public void setReggroup(String reggroup) {
		this.reggroup = reggroup;
	}
    
    
}
