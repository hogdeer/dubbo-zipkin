package com.hogdeer.extend.common.brave.dubbo.support;

import com.alibaba.dubbo.rpc.RpcContext;
import com.hogdeer.extend.common.brave.dubbo.DubboServerNameProvider;

/**
 * 解析dubbo Provider applicationName
 * <p>File：DefaultServerNameProvider.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年10月8日 下午1:59:21</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class DefaultServerNameProvider implements DubboServerNameProvider { 

    public String resolveServerName(RpcContext rpcContext) {
         String application = RpcContext.getContext().getUrl().getParameter("application");
         return application;
    }
}
