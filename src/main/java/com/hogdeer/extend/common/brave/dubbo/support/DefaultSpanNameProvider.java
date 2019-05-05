package com.hogdeer.extend.common.brave.dubbo.support;

import com.alibaba.dubbo.rpc.RpcContext;
import com.hogdeer.extend.common.brave.dubbo.DubboSpanNameProvider;

/**
 * <p>File：DefaultSpanNameProvider.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年10月8日 下午1:59:44</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class DefaultSpanNameProvider implements DubboSpanNameProvider { 

    public String resolveSpanName(RpcContext rpcContext) {
        String className = rpcContext.getUrl().getPath();
        String simpleName = className.substring(className.lastIndexOf(".")+1);
        return simpleName+"."+rpcContext.getMethodName();

    }
}
