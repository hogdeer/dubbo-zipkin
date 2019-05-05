/*
 */
package com.hogdeer.extend.common.dubbo;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.hogdeer.extend.common.bean.DiamondConfig;
import com.hogdeer.extend.common.bean.DubboPublishBean;
import com.hogdeer.extend.common.utils.SpringContextUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;


/**
 */
public class PublishDesider implements IPublishDesider{

	public static final String PUBLISH_FILTER_KEY = "publish_filter_key";
	public static final String PUBLISH_FILTER_SUFFIX = "dubbo.publish.filter.";
	public static final String GRAY_COOKIE_RULE_KEY = "gray_rule_shunt_key";

	/* (non-Javadoc)
	 * @see com.olymtech.shopkeeper.common.bean.dubbo.IVersionDesider#desideVersion(com.alibaba.dubbo.config.ReferenceConfig)
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public String desideVersion(ReferenceConfig referenceConfig, String publishFilter) {
		String reggroup = null;
		DiamondConfig diamondConfig = null;
    	Object obj = SpringContextUtils.getBean("diamondConfig");
    	if(null != obj){
    		diamondConfig = (DiamondConfig) obj;
    	}
		if(null != diamondConfig){
			Map<String, Object> configMap = diamondConfig.getConfigMap();
			if(StringUtils.isNotBlank(publishFilter) && MapUtils.isNotEmpty(configMap)){
				DubboPublishBean dubboPublishBean = JSON.parseObject(publishFilter, DubboPublishBean.class);
				String[] keyAry = {"*", dubboPublishBean.getCookieValue(), dubboPublishBean.getIp(), 
						dubboPublishBean.getUsername(), dubboPublishBean.getOrgId()};
				for(String key:keyAry){
					reggroup = this.getReggroup(configMap, key, referenceConfig.getServicename());
					if(StringUtils.isNotBlank(reggroup)){
						return reggroup;
					}
				}
				/*reggroup = this.getReggroup(referenceConfig, configMap, dubboPublishBean.getOrgId(), reggroup);
				reggroup = this.getReggroup(referenceConfig, configMap, dubboPublishBean.getUsername(), reggroup);
				reggroup = this.getReggroup(referenceConfig, configMap, dubboPublishBean.getIp(), reggroup);
				reggroup = this.getReggroup(referenceConfig, configMap, dubboPublishBean.getCookieValue(), reggroup);
				reggroup = this.getReggroup(referenceConfig, configMap, "*", reggroup);*/
			}
		}
		return reggroup;
	}
	
	private String getReggroup(Map<String, Object> configMap, String key, String servicename){
		if(StringUtils.isNotBlank(key)){
			key = PUBLISH_FILTER_SUFFIX + key;
			if(configMap.containsKey(key) && StringUtils.isNotBlank(servicename)){
				Map<String, String> searchMap = Maps.newHashMap();
				String group = (String) configMap.get(key);
				String[] groupAry = group.split("\\|\\|");  
				if(ArrayUtils.isNotEmpty(groupAry)){
					for(String value:groupAry){
						if(StringUtils.isNotBlank(value)){
							String[] valueAry = value.split("\\|");
							if(ArrayUtils.isNotEmpty(valueAry) && valueAry.length == 2){
								String[] serviceNameAry = valueAry[1].split(",");
								for(String item:serviceNameAry){
									searchMap.put(item.trim(), valueAry[0].trim());
								}
							}
						}
					}
					String[] strAry = {servicename, "*"};
					for(String item:strAry){
						if(searchMap.containsKey(item)){
							return searchMap.get(item);
						}
					}
				}
			}
		}
		return null; 
	}
}
