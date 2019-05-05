
package com.hogdeer.extend.common.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;
import com.hogdeer.extend.common.bean.DiamondConfig;
import com.hogdeer.extend.common.utils.DubboUtils;
import com.hogdeer.extend.common.utils.SpringContextUtils;
import com.hogdeer.extend.common.utils.StringHandleUtils;
//import com.olymtech.shopkeeper.common.bean.SpringContextUtils;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * <p>File：ReferenceBean.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年11月22日 下午6:45:08</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class ReferenceBean<T> extends com.alibaba.dubbo.config.spring.ReferenceBean<T> implements Cloneable{
	
	private static final long serialVersionUID = 8383612147446385736L;
	
	private final static Logger logger = LoggerFactory.getLogger(ReferenceBean.class);
	private transient T proxy;//代理
	
	private transient IPublishDesider publishDesider;//版本决定器 
	private transient ApplicationContext applicationContext;
	private transient ReferenceBean self_=this;
	private transient ConcurrentHashMap<String,com.alibaba.dubbo.config.spring.ReferenceBean> groupMap = new ConcurrentHashMap<String,com.alibaba.dubbo.config.spring.ReferenceBean>();
	private transient final String DEFAULT_GROUP="dubbo";

	@SuppressWarnings({ "all"})
	public void setApplicationContext(ApplicationContext applicationContext){
		super.setApplicationContext(applicationContext);
		this.applicationContext=applicationContext;
	}
	@SuppressWarnings({ "all"})
    public void afterPropertiesSet() throws Exception{
		super.afterPropertiesSet();
		if(applicationContext.containsBean("publishDesider")){
			publishDesider = (IPublishDesider) applicationContext.getBean("publishDesider");
		}else{
			publishDesider = null;
		}
		proxy=getVersionDecideProxy();//创建代理对象
	}
	/**
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "all"})
	private T getVersionDecideProxy() throws Exception{
		// 创建代理工厂  
        ProxyFactory proxyFactory = new ProxyFactory();
        Class[] interfaces={Class.forName(this.getInterface())};
        proxyFactory.setInterfaces(interfaces);
        // 创建代理类型的Class
        Class<ProxyObject> proxyClass = proxyFactory.createClass(); 
        T proxy = (T) proxyClass.newInstance();  
        ((ProxyObject) proxy).setHandler(new MethodHandler() {  
            @Override  
            public T invoke(Object self, Method thisMethod,  
                    Method proceed, Object[] args) throws Throwable {  
            	T target = null;
            	String reggroup=self_.getReggroup();
            	reggroup = getCurReggroup(self_.getServicename(), reggroup); 
            	String publishFilter = RpcContext.getContext().getAttachment(PublishDesider.PUBLISH_FILTER_KEY);
        		if (publishDesider!=null){
        			String tempReggroup=publishDesider.desideVersion(self_, publishFilter);  
        			if(StringUtils.isNotBlank(tempReggroup)){
        				reggroup = tempReggroup;
        			}
        		}
        		if(StringUtils.isBlank(reggroup)) reggroup=DEFAULT_GROUP;
        		String applicationName = self_.getApplication().getName();
        		StringBuilder sb = new StringBuilder("客户端工程：");
        		sb.append(applicationName).append("调用服务端工程：").append(self_.getServicename());
        		sb.append("，服务端工程组名：").append(reggroup).append("，客户信息：").append(publishFilter);
        		logger.info(sb.toString());
        		com.alibaba.dubbo.config.spring.ReferenceBean referenceBean = null;
    			synchronized(self_){
    				String referenceKey = DubboUtils.getReferenceCacheKey(reggroup, self_.getGroup(), self_.getInterface(), self_.getVersion());
    				referenceBean = groupMap.get(referenceKey);
        			if (referenceBean==null){
        				/*referenceBean = (com.alibaba.dubbo.config.spring.ReferenceBean) self_.clone();
        				referenceBean.setReggroup(reggropu);
        				referenceBean.setCheck(Boolean.FALSE);*/
        				referenceBean = new com.alibaba.dubbo.config.spring.ReferenceBean();
        				referenceBean.setApplicationContext(applicationContext);
        	            referenceBean.setInterface(self_.getInterface());
        	            referenceBean.setUrl(self_.getUrl());
        	            referenceBean.setReggroup(reggroup);
						referenceBean.setGroup(self_.getGroup());
						referenceBean.setVersion(self_.getVersion());
						referenceBean.setCheck(Boolean.FALSE);
						referenceBean.setServicename(self_.getServicename());
        				referenceBean.afterPropertiesSet();
        				groupMap.put(referenceKey, referenceBean);  
        			} 
        			target = (T)referenceBean.get();
    			}
    			try{
	                T retObj = (T)thisMethod.invoke(target, args);   
	                if(StringUtils.isNotBlank(publishFilter)){
	                	RpcContext.getContext().setAttachment(PublishDesider.PUBLISH_FILTER_KEY, publishFilter);
	                }
	                return retObj; 
    			}catch(InvocationTargetException e){
    				throw e.getTargetException();
    			}
            }
        });
        return proxy;
	}
	
	public Object getObject() throws Exception { 
        return proxy;
    }
	
	public String getCurReggroup(String serviceName, String reggroup){
		String tempReggroup = null;
		try{
			if(StringUtils.isNotBlank(serviceName)){
				String diamondName = "dubboConfig";
				if(SpringContextUtils.containsBean(diamondName)){
					Object obj = SpringContextUtils.getBean(diamondName);
					if(null != obj){
						DiamondConfig dubboConfig = (DiamondConfig) obj;
						String diamondKey = StringHandleUtils.connectString(serviceName, "reggroup", ".");
						if(dubboConfig.getConfigMap().containsKey(diamondKey)){
							tempReggroup = String.valueOf(dubboConfig.getConfigMap().get(diamondKey));
							int starSize = tempReggroup.indexOf("${");
							if(starSize >= 0){
								starSize += 2;
								int endSize = tempReggroup.indexOf("}");
								if(endSize >= 0 && endSize > starSize){
									tempReggroup = tempReggroup.substring(starSize, endSize);
								}
								if(dubboConfig.getConfigMap().containsKey(tempReggroup)){
									tempReggroup = String.valueOf(dubboConfig.getConfigMap().get(tempReggroup));
								}else{
									tempReggroup = null;
								}
							}
						}
			    	}
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		if(StringUtils.isNotBlank(tempReggroup)){
			reggroup = tempReggroup;
		}
		return reggroup;
	}
}
