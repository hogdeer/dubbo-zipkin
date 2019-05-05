/*
 * @(#)CusMySQLStatementInterceptorManagementBean.java 2017年9月29日 下午3:09:55
 * Copyright 2017 施建波, Inc. All rights reserved. cargogm.com
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.brave.mysql;

import com.github.kristofa.brave.ClientTracer;

import java.io.Closeable;
import java.io.IOException;

/**
 * <p>File：CusMySQLStatementInterceptorManagementBean.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年9月29日 下午3:09:55</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class CusMySQLStatementInterceptorManagementBean implements Closeable {


	/*public CusMySQLStatementInterceptorManagementBean(final ClientTracer tracer) {
        MySQLStatementInterceptor.setClientTracer(tracer);
    }

	@Override
    public void close() throws IOException {
    	MySQLStatementInterceptor.setClientTracer(null);
    }*/
	//TracingStatementInterceptor
	/*public CusMySQLStatementInterceptorManagementBean(final ClientTracer tracer) {
		CusMySQLStatementInterceptor.setClientTracer(tracer);
    }

	@Override
    public void close() throws IOException {
    	CusMySQLStatementInterceptor.setClientTracer(null); 
    }*/
	
	public CusMySQLStatementInterceptorManagementBean(final ClientTracer tracer) {
		CusMySQLStatementInterceptor.setClientTracer(tracer);
    }

	@Override
    public void close() throws IOException {
		CusMySQLStatementInterceptor.setClientTracer(null); 
    }
	
	
}
