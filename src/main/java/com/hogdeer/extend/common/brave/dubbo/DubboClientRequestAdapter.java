package com.hogdeer.extend.common.brave.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.github.kristofa.brave.ClientRequestAdapter;
import com.github.kristofa.brave.IdConversion;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.SpanId;
import com.google.common.collect.Lists;
import com.hogdeer.extend.common.brave.IPConversion;
import com.hogdeer.extend.common.brave.dubbo.support.DefaultServerNameProvider;
import com.hogdeer.extend.common.brave.dubbo.support.DefaultSpanNameProvider;
import com.twitter.zipkin.gen.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zipkin.internal.Nullable;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

/**
 * 
 * <p>File：DubboClientRequestAdapter.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
public class DubboClientRequestAdapter implements ClientRequestAdapter { 
	
	private final static Logger logger = LoggerFactory.getLogger(DubboClientRequestAdapter.class);
	
    private Invoker<?> invoker;
    private Invocation invocation;
    private final static DubboSpanNameProvider spanNameProvider = new DefaultSpanNameProvider();
    private final static DubboServerNameProvider serverNameProvider = new DefaultServerNameProvider();


    public DubboClientRequestAdapter(Invoker<?> invoker, Invocation invocation) {
        this.invoker = invoker;
        this.invocation = invocation;
    }

    public String getSpanName() {
        return spanNameProvider.resolveSpanName(RpcContext.getContext());
    }

    public void addSpanIdToRequest(@Nullable SpanId spanId) {
        String application = RpcContext.getContext().getUrl().getParameter("application");
        RpcContext.getContext().setAttachment("clientName", application);
        if (spanId == null) {
            RpcContext.getContext().setAttachment("sampled", "0");
        }else{
            RpcContext.getContext().setAttachment("traceId", IdConversion.convertToString(spanId.traceId));
            RpcContext.getContext().setAttachment("spanId", IdConversion.convertToString(spanId.spanId));
            if (spanId.nullableParentId() != null) {
                RpcContext.getContext().setAttachment("parentId", IdConversion.convertToString(spanId.parentId));
            }
        }
    }

    public Collection<KeyValueAnnotation> requestAnnotations() { 
    	//Collection<KeyValueAnnotation> collection = Collections.singletonList(KeyValueAnnotation.create("url", RpcContext.getContext().getUrl().toString()));
    	List<KeyValueAnnotation> list = Lists.newArrayList();
    	try{
    		InetSocketAddress socketAddress = RpcContext.getContext().getLocalAddress(); 
    		if (socketAddress != null) {
	            KeyValueAnnotation remoteAddrAnnotation = KeyValueAnnotation.create("address.client", socketAddress.toString());
	            list.add(remoteAddrAnnotation);
    		}
	    	KeyValueAnnotation paramAnnotation = KeyValueAnnotation.create("params", JSON.toJSONString(invocation.getArguments()));
	        KeyValueAnnotation urlAnnotation = KeyValueAnnotation.create("url", RpcContext.getContext().getUrl().toString());
	        list.add(paramAnnotation);
	        list.add(urlAnnotation);
        }catch(Exception e){
        	logger.error(e.getMessage(), e);
        }
        return list;
    }

    public Endpoint serverAddress() {
        InetSocketAddress inetSocketAddress = RpcContext.getContext().getRemoteAddress();
        String ipAddr = RpcContext.getContext().getUrl().getIp();
        String serverName = serverNameProvider.resolveServerName(RpcContext.getContext());
        return Endpoint.create(serverName, IPConversion.convertToInt(ipAddr),inetSocketAddress.getPort());
    }



}
