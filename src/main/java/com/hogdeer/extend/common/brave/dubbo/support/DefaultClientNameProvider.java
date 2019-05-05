package com.hogdeer.extend.common.brave.dubbo.support;

import com.alibaba.dubbo.rpc.RpcContext;
import com.hogdeer.extend.common.brave.dubbo.DubboClientNameProvider;

/**
 * 解析dubbo consumer applicationName
 * <p>File：DefaultClientNameProvider.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
public class DefaultClientNameProvider implements DubboClientNameProvider { 
	
    public String resolveClientName(RpcContext rpcContext) {
        String application = RpcContext.getContext().getUrl().getParameter("clientName");
        return application; 
    }
}
