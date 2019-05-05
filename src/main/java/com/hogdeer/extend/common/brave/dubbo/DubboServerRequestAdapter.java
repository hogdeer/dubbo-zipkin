package com.hogdeer.extend.common.brave.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.github.kristofa.brave.*;
import com.google.common.collect.Lists;
import com.hogdeer.extend.common.brave.IPConversion;
import com.hogdeer.extend.common.brave.dubbo.support.DefaultClientNameProvider;
import com.hogdeer.extend.common.brave.dubbo.support.DefaultSpanNameProvider;
import com.hogdeer.extend.common.brave.utils.BraveUtils;
import com.hogdeer.extend.common.consts.BraveConst;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

import static com.github.kristofa.brave.IdConversion.convertToLong;

/**
 * 
 * <p>File：DubboServerRequestAdapter.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
public class DubboServerRequestAdapter  implements ServerRequestAdapter {
	
	private final static Logger logger = LoggerFactory.getLogger(DubboServerRequestAdapter.class);

    private Invoker<?> invoker;
    private Invocation invocation;
    private ServerTracer serverTracer; 
    private final static  DubboSpanNameProvider spanNameProvider = new DefaultSpanNameProvider();
    private final static  DubboClientNameProvider clientNameProvider = new DefaultClientNameProvider();

    public DubboServerRequestAdapter(Invoker<?> invoker, Invocation invocation,ServerTracer serverTracer) {
        this.invoker = invoker;
        this.invocation = invocation;
        this.serverTracer = serverTracer;
    }

    public TraceData getTraceData() {
      String sampled =   invocation.getAttachment("sampled");
      if(sampled != null && sampled.equals("0")){
          return TraceData.builder().sample(false).build();
      }else {
          final String parentId = invocation.getAttachment("parentId");
          final String spanId = invocation.getAttachment("spanId");
          final String traceId = invocation.getAttachment("traceId");
          if (traceId != null && spanId != null) {
              SpanId span = getSpanId(traceId, spanId, parentId);
              return TraceData.builder().sample(true).spanId(span).build();
          }
      }
       return TraceData.builder().build();

    }

    public String getSpanName() {
        return spanNameProvider.resolveSpanName(RpcContext.getContext());
    }

    public Collection<KeyValueAnnotation> requestAnnotations() {
    	List<KeyValueAnnotation> list = Lists.newArrayList();
    	try{
	        String ipAddr = RpcContext.getContext().getUrl().getIp();
	        InetSocketAddress inetSocketAddress = RpcContext.getContext().getRemoteAddress();
	        final String clientName = clientNameProvider.resolveClientName(RpcContext.getContext());
	        if(StringUtils.isNotBlank(clientName)){   
	        	serverTracer.setServerReceived(IPConversion.convertToInt(ipAddr),inetSocketAddress.getPort(),clientName);
	        }
	        KeyValueAnnotation projectAnnotation = KeyValueAnnotation.create(BraveConst.BRAVE_PROJECT_TYPE, "dubbo");
	        list.add(projectAnnotation);
	        BraveUtils.setProjectTypeAnnotation(list, BraveUtils.PROJECT_TYPE_NAME);
	        InetSocketAddress socketAddress = RpcContext.getContext().getLocalAddress();
	        if (socketAddress != null) {
	            KeyValueAnnotation remoteAddrAnnotation = KeyValueAnnotation.create("address.service", socketAddress.toString());
	            list.add(remoteAddrAnnotation);
	            if(!isClientBrave()){
	            	KeyValueAnnotation paramAnnotation = KeyValueAnnotation.create("params", JSON.toJSONString(invocation.getArguments()));
	            	list.add(paramAnnotation);
	            }
	        } 
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
    	}
    	return list;
    }

    static SpanId getSpanId(String traceId, String spanId, String parentSpanId) {
        return SpanId.builder()
                .traceId(convertToLong(traceId))
                .spanId(convertToLong(spanId))
                .parentId(parentSpanId == null ? null : convertToLong(parentSpanId)).build();
    }
    
    private Boolean isClientBrave(){
    	String isClientBrave = RpcContext.getContext().getAttachment("isClientBrave");
    	if(StringUtils.isNotBlank(isClientBrave)){
    		return true;
    	}
    	return false;
    }
}
