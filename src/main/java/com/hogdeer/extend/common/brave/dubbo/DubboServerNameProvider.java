package com.hogdeer.extend.common.brave.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;

/**
 * 
 * <p>File：DubboServerNameProvider.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
public interface DubboServerNameProvider {  
    public String resolveServerName(RpcContext rpcContext);
}
