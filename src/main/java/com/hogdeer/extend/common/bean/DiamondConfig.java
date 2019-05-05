package com.hogdeer.extend.common.bean;

import com.google.common.collect.Maps;
import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * <p>File：DiamondConfig.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年11月23日 下午2:47:34</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class DiamondConfig implements Serializable{

	private static final Logger logger = LoggerFactory.getLogger(DiamondConfig.class);
	
	//
	private static final long serialVersionUID = 1964470553673629538L;
	
	//重试次数
	private Integer retry=3;
	//组名
	private String group;
	//文件ID
	private String dataId;
	
	private String projectName;
	
	private Map<String, Object> configMap = Maps.newHashMap();

	private DiamondManager manager = null;

	public void setGroup(String group) {
		this.group = group;
	}
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	public Map<String, Object> getConfigMap() {
		return configMap;
	}
	
	public void configInit(){
		manager = new DefaultDiamondManager(this.group, this.dataId, new ConfigManagerListener(this));
		String configInfo = readConfig(dataId);
		initMap(configInfo);
	}
	
	private String readConfig(final String key) {
		for(int i=0;i<retry;i++){
			try{
				return this.manager.getAvailableConfigureInfomation(2000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void initMap(String configInfo){
		if(StringUtils.isNotBlank(configInfo)){
			try {
				configMap.clear();
				InputStreamReader inread = new InputStreamReader(new ByteArrayInputStream(configInfo.getBytes()), "utf-8");
				Properties prop = new Properties();
				prop.load(inread);
				Enumeration<?> enumeration = prop.propertyNames(); 
				while(enumeration.hasMoreElements()){  
	                String key = (String) enumeration.nextElement(); 
	                configMap.put(key, prop.getProperty(key.trim())); 
	            } 
			}
			catch (Exception e) {
				logger.error("Diamond加载失败! configInfo:"+configInfo);
			}
		}
	}

//	public boolean push(String configInfo){
//		return this.manager.pushConfigureInfomation(configInfo, 2000);
//	}
	
	class ConfigManagerListener implements ManagerListener{
		
		private DiamondConfig diamondConfig;
		
		ConfigManagerListener(DiamondConfig diamondConfig){
			this.diamondConfig = diamondConfig;
		}

		/* (non-Javadoc)
		 * @see com.taobao.diamond.manager.ManagerListener#getExecutor()
		 */
		@Override
		public Executor getExecutor() {
			return null;
		}

		/* (non-Javadoc)
		 * @see com.taobao.diamond.manager.ManagerListener#receiveConfigInfo(java.lang.String)
		 */
		@Override
		public void receiveConfigInfo(String configInfo) {
			logger.info("工程:"+projectName+"，diamond更新成功!"); 
			this.diamondConfig.initMap(configInfo);
		}
	}
}
