package com.hogdeer.extend.common.brave.dubbo;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.LoggingSpanCollector;
import com.github.kristofa.brave.Sampler;
import com.github.kristofa.brave.http.HttpSpanCollector;
import com.hogdeer.extend.common.bean.DiamondConfig;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.kafka08.KafkaSender;
import zipkin.storage.mysql.MySQLStorage;

import java.util.Map;
import java.util.logging.Logger;

/**
 * BraveBean管理器
 * <p>File：BraveFactoryBean.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
public class BraveFactoryBean implements FactoryBean<Brave> { 
    private static final Logger LOGGER = Logger.getLogger(BraveFactoryBean.class.getName());
    /**服务名*/
    private String serviceName;
    /**zipkin服务器ip及端口，不配置默认打印日志*/ 
    private String zipkinHost;
    /**采样率 0~1 之间*/
    private float rate = 1.0f;
    /**单例模式*/
    private Brave instance;
    
    private DiamondConfig braveConfig;
    /**发送类型：zipkin、kafka,默认为zipkin*/
    private String senderType;
    /**kafka服务器ip及端口*/
    private String kafkaHost;

    private  MysqlDataSource mysqlDataSource;

    public MysqlDataSource getMysqlDataSource() {
        return mysqlDataSource;
    }

    public void setMysqlDataSource(MysqlDataSource mysqlDataSource) {
        this.mysqlDataSource = mysqlDataSource;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getZipkinHost() {
        return zipkinHost;
    }

    public String getRate() {
        return String.valueOf(rate);
    }

    public void setRate(String rate) {
        this.rate = Float.parseFloat(rate);
    }

    public void setZipkinHost(String zipkinHost) {
        this.zipkinHost = zipkinHost;
    }

    public String getSenderType() {
		return senderType;
	}

	public void setSenderType(String senderType) {
		this.senderType = senderType;
	}

	public DiamondConfig getBraveConfig() {
		return braveConfig;
	}

	public void setBraveConfig(DiamondConfig braveConfig) {
		this.braveConfig = braveConfig;
	}

	private void createInstance() {
        if (this.serviceName == null) {
            throw new BeanInitializationException("Property serviceName must be set.");
        }
        Brave.Builder builder = new Brave.Builder(this.serviceName);
        if(StringUtils.isNotBlank(this.senderType) && "kafka".equals(senderType)){
        	KafkaSender sender = KafkaSender.builder().bootstrapServers(this.kafkaHost).build();
            AsyncReporter<Span> report = AsyncReporter.builder(sender).build();
            builder = builder.reporter(report);
        }else{
	        if (this.zipkinHost != null && !"".equals(this.zipkinHost)) {
	            builder.spanCollector(HttpSpanCollector.create(this.zipkinHost, new EmptySpanCollectorMetricsHandler())).traceSampler(Sampler.create(rate)).build();
	            LOGGER.info("brave dubbo config collect whith httpSpanColler , rate is "+ rate);
	        }else{
	            builder.spanCollector(new LoggingSpanCollector()).traceSampler(Sampler.create(rate)).build();
	            LOGGER.info("brave dubbo config collect whith loggingSpanColletor , rate is "+ rate);
	        }

	        if ("mysql".equals(senderType)){
                MySQLStorage.builder().datasource(mysqlDataSource).executor(Runnable::run).build();
            }



        }

        this.instance = builder.build();
    }

    public Brave getObject() throws Exception {
        if (this.instance == null) {
            this.createInstance();
        }
        return this.instance;
    }


    public Class<?> getObjectType() {
        return Brave.class;
    }

 
    public boolean isSingleton() {
        return true;
    }
    
    public void configInit(){
    	Map<String, Object> configMap = this.braveConfig.getConfigMap();
    	this.zipkinHost = (String) configMap.get("brave.zipkinHost");
    	this.rate = Float.valueOf(configMap.get("brave.rate").toString());
    	Object obj = configMap.get("brave.senderType");
    	if(null != obj){
    		this.senderType = obj.toString();
    	}

        Object objkafka= configMap.get("brave.kafkaHost");
    	if (null!=objkafka){
            this.kafkaHost =objkafka.toString();
        }


    	mysqlDataSource=new MysqlDataSource();
        mysqlDataSource.setURL("jdbc:mysql://localhost:3306/zipkin?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("root");

	}
}
