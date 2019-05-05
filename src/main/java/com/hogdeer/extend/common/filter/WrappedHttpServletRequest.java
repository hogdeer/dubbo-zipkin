/*
 * @(#)WrappedHttpServletRequest.java 2017年4月13日 上午11:07:24
 * Copyright 2017 施建波, Inc. All rights reserved. 积木科技
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.hogdeer.extend.common.filter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>File：WrappedHttpServletRequest.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2017年4月13日 上午11:07:24</p>
 * <p>Company: 积木科技</p>
 * @author 施建波
 * @version 1.0
 */
public class WrappedHttpServletRequest extends HttpServletRequestWrapper
{
	private static final Logger logger = LoggerFactory.getLogger(WrappedHttpServletRequest.class);
	
    private byte[] bytes;
    private WrappedServletInputStream wrappedServletInputStream;

    public WrappedHttpServletRequest(HttpServletRequest request) throws IOException {
    	super(request);
    	try{
	        bytes = this.reaplBytes(request);
	        if(null != bytes){
		        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		        this.wrappedServletInputStream = new WrappedServletInputStream(byteArrayInputStream);
		        reWriteInputStream();
	        }
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
    	}
    }
    
    public void reWriteInputStream() {
        wrappedServletInputStream.setStream(new ByteArrayInputStream(bytes != null ? bytes : new byte[0]));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return wrappedServletInputStream;
    }

    /**
     * 获取post参数，可以自己再转为相应格式
     */
    public String getRequestParams() throws IOException {
    	String param = StringUtils.EMPTY;
    	try{
    		if(null != this.bytes){
    			param = new String(bytes, this.getCharacterEncoding());
    		}
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
    	}
        return param;
    }
    
    private byte[] reaplBytes(HttpServletRequest request){
    	try{
    		String param = IOUtils.toString(request.getInputStream(), this.getCharacterEncoding());
    		/*if(StringUtils.isNotBlank(param)){
    			String result = "\"\"";
    			Pattern pattern=Pattern.compile(result);
    	    	Matcher mat = pattern.matcher(param);
    	    	param =mat.replaceAll("null");
    		}*/
    		return param.getBytes(this.getCharacterEncoding());
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
    	}
    	return null;
    }

    private class WrappedServletInputStream extends ServletInputStream {
        
        private InputStream stream;
        
        public void setStream(InputStream stream) {
            this.stream = stream;
        }

        public WrappedServletInputStream(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}
