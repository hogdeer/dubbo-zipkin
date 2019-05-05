package com.hogdeer.extend.common.brave.dubbo;

import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.ServerResponseAdapter;
import com.hogdeer.extend.common.brave.utils.BraveUtils;
import com.hogdeer.extend.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import zipkin.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * <p>File：DubboServerResponseAdapter.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
public class DubboServerResponseAdapter implements ServerResponseAdapter { 

    private Result rpcResult ;

    public DubboServerResponseAdapter(Result rpcResult) { 
        this.rpcResult = rpcResult;
    }

    public Collection<KeyValueAnnotation> responseAnnotations() {
        List<KeyValueAnnotation> annotations = new ArrayList<KeyValueAnnotation>();
        if(!rpcResult.hasException()){
            KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("server_result","true");
            annotations.add(keyValueAnnotation);
        }else {
        	if(!isClientBrave()){
	        	String errMsg = "";
	        	if(rpcResult.getException() instanceof BusinessException){
	        		BusinessException busException = (BusinessException) rpcResult.getException();
	        		errMsg = busException.getNativeMessage();
	        	}else{
	        		errMsg = ExceptionUtils.getStackTrace(rpcResult.getException());
	        	}
	        	KeyValueAnnotation errValueAnnotation=  KeyValueAnnotation.create(Constants.ERROR, "1");
	        	annotations.add(errValueAnnotation);
	        	if(StringUtils.isNotBlank(errMsg)){
		            KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("error.msg", errMsg);
		            annotations.add(keyValueAnnotation);
		            BraveUtils.setExceptionTypeAnnotaion(annotations, rpcResult.getException());
	        	}
        	}
        }
        return annotations;
    }
    
    private Boolean isClientBrave(){
    	String isClientBrave = RpcContext.getContext().getAttachment("isClientBrave");
    	if(StringUtils.isNotBlank(isClientBrave)){
    		return true;
    	}
    	return false;
    }
}
