package com.hogdeer.extend.common.brave.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ClientRequestInterceptor;
import com.github.kristofa.brave.ClientResponseInterceptor;
import com.github.kristofa.brave.ClientSpanThreadBinder;

/**
 * dubbo客户端过滤器
 * <p>File：BraveConsumerFilter.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
@Activate(group = Constants.CONSUMER)  
public class BraveConsumerFilter implements Filter {


    private static volatile Brave brave;
    private static volatile String clientName;
    private static volatile ClientRequestInterceptor clientRequestInterceptor;
    private static volatile ClientResponseInterceptor clientResponseInterceptor;
    private static volatile ClientSpanThreadBinder clientSpanThreadBinder; 

    public static void setBrave(Brave brave) {
        BraveConsumerFilter.brave = brave;
        BraveConsumerFilter.clientRequestInterceptor = brave.clientRequestInterceptor();
        BraveConsumerFilter.clientResponseInterceptor = brave.clientResponseInterceptor();
        BraveConsumerFilter.clientSpanThreadBinder = brave.clientSpanThreadBinder();
    }

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    	/*String publishFilter = RpcContext.getContext().getAttachment(PublishDesider.PUBLISH_FILTER_KEY);
    	if(StringUtils.isNotBlank(publishFilter)){
    		RpcContext.getContext().setAttachment(PublishDesider.PUBLISH_FILTER_KEY, publishFilter);
    	}*/
    	if(null != this.brave){
    		RpcContext.getContext().setAttachment("isClientBrave", "1"); 
	        clientRequestInterceptor.handle(new DubboClientRequestAdapter(invoker,invocation));
	        try{
	            Result rpcResult = invoker.invoke(invocation); 
	            clientResponseInterceptor.handle(new DubboClientResponseAdapter(rpcResult));
	            return rpcResult;
	        }catch (Exception ex){
	            //clientResponseInterceptor.handle(new DubboClientResponseAdapter(ex));
	            throw new RpcException(ex.getMessage(), ex);
	        }finally {
	            clientSpanThreadBinder.setCurrentSpan(null);
	        }
    	}else{
    		return invoker.invoke(invocation);
    	}
    }
}
