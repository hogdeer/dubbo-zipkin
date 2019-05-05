package com.hogdeer.extend.common.brave.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;

/**
 *
 * <p>File：DubboSpanNameProvider.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
public interface DubboSpanNameProvider {   

     String resolveSpanName(RpcContext rpcContext);
}
