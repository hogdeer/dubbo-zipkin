package com.hogdeer.extend.common.brave.mysql;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.github.kristofa.brave.ClientTracer;
import com.github.kristofa.brave.IdConversion;
import com.github.kristofa.brave.SpanId;
import com.google.common.collect.Maps;
import com.hogdeer.extend.common.bean.BaseEntity;
import com.hogdeer.extend.common.bean.DiamondConfig;
import com.hogdeer.extend.common.utils.ArrayHandleUtils;
import com.hogdeer.extend.common.utils.SpringContextUtils;
import com.hogdeer.extend.common.utils.ValidateUtils;
//import com.olymtech.logservice.dsapi.util.LogUtils;
//import com.olymtech.logservice.dsapi.vo.DataLogTempVo;
//import com.olymtech.shopkeeper.common.bean.SpringContextUtils;
import com.twitter.zipkin.gen.Endpoint;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zipkin.Constants;
import zipkin.TraceKeys;

import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.google.gson.Gson;

/**
 * mybits拦截器
 * <p>File：CusMyBitsStatementInterceptor.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年9月30日 上午9:46:13</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
@Intercepts({
    @Signature(type=Executor.class,method="update",args={MappedStatement.class,Object.class}),
    @Signature(type=Executor.class,method="query",args={MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class})
})
public class CusMyBitsStatementInterceptor implements Interceptor{
	
	private final static Logger logger = LoggerFactory.getLogger(CusMyBitsStatementInterceptor.class);

	private ClientTracer clientTracer;
	
	private DiamondConfig braveConfig;
	
	public void setClientTracer(ClientTracer clientTracer) {
		this.clientTracer = clientTracer;
	}
	
	public void setBraveConfig(DiamondConfig braveConfig) {
		this.braveConfig = braveConfig;
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.plugin.Interceptor#intercept(org.apache.ibatis.plugin.Invocation)
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		
		ClientTracer clientTracer = this.clientTracer; 
		Object result;
		if (clientTracer != null) {
			Connection connection = null;
			Object paramObj = null;
			String commandName = null;
			Long traceId = null;
			String className = null;
			String sql = null;
			String param = null;
			Boolean isSendMsg = Boolean.FALSE;
			try{
				Executor executor = (Executor) invocation.getTarget();
				connection = executor.getTransaction().getConnection();
				final MappedStatement interceptedStatement = (MappedStatement) invocation.getArgs()[0];
				commandName = interceptedStatement.getSqlCommandType().name(); 
				paramObj = invocation.getArgs()[1];
				
				BoundSql boundSql = interceptedStatement.getBoundSql(paramObj);
				sql = boundSql.getSql();
				sql = sql.replaceAll("\n", "");
				sql = sql.replaceAll("  ", " ");
				Map<String, String> sqlParamMap = this.getSqlparam(paramObj);
				param = sqlParamMap.get("param");
				className = sqlParamMap.get("className");
				traceId = beginTrace(clientTracer, connection, commandName, sql, param);
				isSendMsg = Boolean.TRUE;
			}catch(Exception e){
				logger.error(e.getMessage(), e);
				//endTrace(clientTracer, e, null);
				//return invocation.proceed();
			}
			/*result = invocation.proceed();
			if(isSendMsg){
				endTrace(clientTracer, null, result);
				this.sendSqlMsg(paramObj, commandName, traceId, className, sql, param);			
			}*/
			try{
				result = invocation.proceed();
				if(isSendMsg){
					endTrace(clientTracer, null, result);
					//this.sendSqlMsg(paramObj, commandName, traceId, className, sql, param);
				}
			}catch(Exception e){
				if(isSendMsg){
					endTrace(clientTracer, e, null); 
				}
				throw e;
			}
		}else{
			result = invocation.proceed();
		}
		return result;
	}
	
	private Long beginTrace(final ClientTracer tracer, final Connection connection, String commandName, final String sql, final String parame){
		if(this.isOpenSql()){
			commandName = (StringUtils.isBlank(commandName)) ? "query":commandName;
			SpanId spanId = tracer.startNewSpan(commandName);
	        tracer.submitBinaryAnnotation(TraceKeys.SQL_QUERY, sql);
	        if(StringUtils.isNotBlank(parame)){
	        	tracer.submitBinaryAnnotation("sql.parames", parame);
	        }
	        try {
	        	this.setClientSent(tracer, connection);
	        }catch(Exception e){
	        	tracer.setClientSent();
	        }
	        return spanId.getTraceId();
		}else{
			return 0L;
		}
    }
	
	private void endTrace(final ClientTracer tracer, final Exception exception, Object result){
		if(this.isOpenSql()){
			try{
				if(null != exception){
					String errMsg = null;
					if(exception instanceof InvocationTargetException){
						errMsg = ExceptionUtils.getStackTrace(((InvocationTargetException) exception).getTargetException());
					}else{
						errMsg = ExceptionUtils.getStackTrace(exception);
					}
					if(StringUtils.isBlank(errMsg)){
						errMsg = "exception.getMessage() is null";
					}
		            tracer.submitBinaryAnnotation(Constants.ERROR, "1");
					tracer.submitBinaryAnnotation("error.msg", errMsg);
				}
				if(null != result){
					List<Object> copyList = ArrayHandleUtils.copyList(result, null);
					if(CollectionUtils.isNotEmpty(copyList)){
						tracer.submitBinaryAnnotation("sql.result", JSON.toJSONString(copyList));
					}else{
						tracer.submitBinaryAnnotation("sql.result", JSON.toJSONString(result));
					}
				}
	        }catch(Exception e){
				logger.error(e.getMessage(), e);
	        }finally{
	        	tracer.setClientReceived();  
	        }
		}
    }
	
	private void setClientSent(ClientTracer tracer, Connection connection) throws Exception {
		String connectionStr = connection.getMetaData().getURL().substring(5);
		int port = -1;
		byte[] addressByte = null;

        String serviceName = connection.getMetaData().getDatabaseProductName().toLowerCase(); 
        if(StringUtils.isBlank(serviceName)){
        	serviceName = "mysql";
        }
        if("mysql".equals(serviceName)){
        	URI url = URI.create(connection.getMetaData().getURL().substring(5)); 
    		InetAddress address = Inet4Address.getByName(url.getHost());
    		addressByte = address.getAddress();
    		port = url.getPort();
        }else{
        	String regEx = "(@.*:[0-9]*:)";
        	Pattern p = Pattern.compile(regEx); 
            Matcher m = p.matcher(connectionStr);
            if(m.find()){
            	connectionStr = m.group();
            	connectionStr = connectionStr.substring(1, connectionStr.length() - 1);
            	String[] connectionStrAry = connectionStr.split(":");
            	InetAddress address = Inet4Address.getByName(connectionStrAry[0]);
            	addressByte = address.getAddress();
            	port = Integer.parseInt(connectionStrAry[1]);
            	/*addressByte = connectionStrAry[0].getBytes();
            	port = Integer.parseInt(connectionStrAry[1]);*/
            }
        } 
        
        int ipv4 = ByteBuffer.wrap(addressByte).getInt();
        port = port == -1 ? 3306 : port;
        
        String databaseName = connection.getCatalog();
        if (StringUtils.isNotBlank(databaseName)) {
        	serviceName += "-" + databaseName;
        }
        tracer.setClientSent(Endpoint.builder()
           .ipv4(ipv4).port(port).serviceName(serviceName).build()); 
   }

	
//	private void sendSqlMsg(Object result, String commandName, Long traceId, String className, String sql, String sqlParam){
//		try{
//			String tableIdName = this.getTableId(className);
//			if(StringUtils.isNotBlank(tableIdName)){
//				if(commandName.startsWith("INSERT") || commandName.startsWith("UPDATE") || commandName.startsWith("DELETE")){
//					String dataId = this.getTableId(result, tableIdName);
//					DataLogTempVo dataChangeLogVo = new DataLogTempVo();
//					//String traceIdStr = IdConversion.convertToString(beginTrace(clientTracer, sql, param));
//					String traceIdStr = IdConversion.convertToString(traceId);
//					dataChangeLogVo.setTrackId(traceIdStr);
//					dataChangeLogVo.setRouteId(Math.abs(traceId));
//					dataChangeLogVo.setDataType(className);
//					dataChangeLogVo.setOperationType(commandName.substring(0,1));
//					dataChangeLogVo.setDataId(dataId);
//					dataChangeLogVo.setDataJson(sqlParam);
//					//logger.info("mybits==========="+new Gson().toJson(dataChangeLogVo));
//			    	LogUtils.addLogToQueue(LogUtils.DATALOG_QUEUE, dataChangeLogVo);
//				}
//			}
//		}catch(Exception e){
//			logger.error(e.getMessage(), e);
//		}
//	}
	
	private Map<String, String> getSqlparam(Object paramObj){
		Map<String, String> sqlParamMap = Maps.newHashMap();
		Map<String, Object> paramMap = Maps.newHashMap();
		//Gson gson = new Gson();
		try{
			if(paramObj instanceof HashMap){
				Map<String, Object> paramObjMap = (HashMap)paramObj;
				//logger.info("paramObjMap:======="+paramObjMap);
				for (Map.Entry<String, Object> entry : paramObjMap.entrySet()) {
					String key = entry.getKey();
					if(!key.startsWith("param") && !"_ORIGINAL_PARAMETER_OBJECT".equals(key)){
						if("ew".equals(key) || "et".equals(key)){
							Object obj = entry.getValue();
							if(obj instanceof Wrapper){
								Wrapper wrapper = (Wrapper) obj;
								String className = "";
								if(null != wrapper.getEntity()){
									className = wrapper.getEntity().getClass().getSimpleName();
									//paramMap.put(className, gson.toJson(wrapper.getEntity()));
									paramMap.put(className, JSON.toJSONString(wrapper.getEntity()));
									
								}
								if(StringUtils.isNotBlank(wrapper.getSqlSegment())){
									paramMap.put("wrapper.sql", wrapper.getSqlSegment());
								}
								if(MapUtils.isNotEmpty(wrapper.getParamNameValuePairs())){
									paramMap.put("wrapper.sql.value", wrapper.getParamNameValuePairs());
								}
								if(StringUtils.isNotBlank(className)){
									sqlParamMap.put("className", className);
								}
							}else{
								String className = obj.getClass().getSimpleName();
								paramMap.put(className, JSON.toJSONString(obj));
								sqlParamMap.put("className", className);
							}
						}else{
							if(!"collection".equals(key)){
								Object obj = entry.getValue();
								if(null != obj){
									List<Object> copyList = ArrayHandleUtils.copyList(obj, null);
									if(CollectionUtils.isNotEmpty(copyList)){
										paramMap.put(key, copyList); 
									}else{
										paramMap.put(key, obj); 
									}
								}
							}
						}
					}
				}
				if(MapUtils.isNotEmpty(paramMap)){
					String param = JSON.toJSONString(paramMap);
					sqlParamMap.put("param", param);
				}
			}else{
				if(null != paramObj){
					String className = paramObj.getClass().getSimpleName();
					sqlParamMap.put("className", className);
					sqlParamMap.put("param", JSON.toJSONString(paramObj));
					//sqlParamMap.put("id", this.getTableId(paramObj)); 
				}
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return sqlParamMap;
	}
	
	private String getTableId(Object obj, String tableIdName) throws Exception{
		if(null != obj && obj instanceof BaseEntity){
			//Map<String, Object> collectTatbleMap = this.getCollectTatbleMap();
			//logger.info("tableId====================="+collectTatbleMap);
			//Object valueObj = collectTatbleMap.get("brave.tableId");
			if(StringUtils.isNotBlank(tableIdName)){
				try{
					String tableId = BeanUtils.getProperty(obj, tableIdName);
					if(StringUtils.isNotBlank(tableId)){
						return tableId;
					}
				}catch(Exception e){
					//logger.error(e.getMessage(), e);
				}
	    	}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.plugin.Interceptor#plugin(java.lang.Object)
	 */
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/* (non-Javadoc)
	 * @see org.apache.ibatis.plugin.Interceptor#setProperties(java.util.Properties)
	 */
	@Override
	public void setProperties(Properties properties) {
	}
	
	private Map<String, Object> getCollectTatbleMap(){
		DiamondConfig collectTableConfig = null;
		if(SpringContextUtils.containsBean("collectTableConfig")){
			collectTableConfig = (DiamondConfig) SpringContextUtils.getBean("collectTableConfig");
		}
    	Map<String, Object> tableNameMap = Maps.newHashMap();
    	if(null != collectTableConfig){
    		tableNameMap = collectTableConfig.getConfigMap();
    	}
    	return tableNameMap;
	}
	
	private String getTableId (String className){
		if(StringUtils.isNotBlank(className)){
			Map<String, Object> collectTatbleMap = this.getCollectTatbleMap();
			//logger.info("tableName====================="+collectTatbleMap);
			Object valueObj = collectTatbleMap.get("brave.tableName");
			if(!ValidateUtils.isObjectNull(valueObj)){
	    		String[] valueAry = valueObj.toString().split("\\|");
	    		for(String value:valueAry){
	    			String[] tempAry = value.split("-");
	    			if(className.equals(tempAry[0])){
	    				if(tempAry.length > 1){
	    					return tempAry[1];
	    				}
	    				return "0";
	    			}
	    		}
	    	}
		}
		return null;
	}
	
	private Boolean isOpenSql(){
		Boolean isOpenSql = Boolean.TRUE;
		if(null != this.braveConfig){
			Object obj = this.braveConfig.getConfigMap().get("brave.isOpenSql");
			if(null != obj){
				isOpenSql = Boolean.valueOf(obj.toString());
			}
		}
		return isOpenSql;
	}
}
