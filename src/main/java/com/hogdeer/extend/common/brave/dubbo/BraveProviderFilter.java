package com.hogdeer.extend.common.brave.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ServerRequestInterceptor;
import com.github.kristofa.brave.ServerResponseInterceptor;
import com.github.kristofa.brave.ServerSpanThreadBinder;

/**
 * dubbo服务器端过滤器
 * <p>File：BraveProviderFilter.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
@Activate(group = Constants.PROVIDER)
public class BraveProviderFilter implements Filter {


    private static volatile Brave brave;
    private static volatile ServerRequestInterceptor serverRequestInterceptor;
    private static volatile ServerResponseInterceptor serverResponseInterceptor;
    private static volatile ServerSpanThreadBinder serverSpanThreadBinder;



    public static void setBrave(Brave brave) {
        BraveProviderFilter.brave = brave;
        BraveProviderFilter.serverRequestInterceptor = brave.serverRequestInterceptor();
        BraveProviderFilter.serverResponseInterceptor = brave.serverResponseInterceptor();
        BraveProviderFilter.serverSpanThreadBinder = brave.serverSpanThreadBinder();
    }


    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    	/*String publishFilter = RpcContext.getContext().getAttachment(PublishDesider.PUBLISH_FILTER_KEY);
    	System.out.println("publishFilter======================="+publishFilter);
    	System.out.println("url======================="+RpcContext.getContext().getUrl());
    	ShardingContextHolder.setDataSourceKey("ds2");*/
    	//ShardingContextHolder.setDataSourceKey("ds2");
    	
    	if(null != serverRequestInterceptor){
    		serverRequestInterceptor.handle(new DubboServerRequestAdapter(invoker,invocation,brave.serverTracer()));
    	}
        Result rpcResult = invoker.invoke(invocation);
        if(null != serverResponseInterceptor){
        	serverResponseInterceptor.handle(new DubboServerResponseAdapter(rpcResult)); 
        }
        return rpcResult;
    }
}
