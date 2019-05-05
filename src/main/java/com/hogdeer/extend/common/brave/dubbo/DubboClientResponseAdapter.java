package com.hogdeer.extend.common.brave.dubbo;

import com.alibaba.dubbo.rpc.Result;
import com.alibaba.fastjson.JSON;
import com.github.kristofa.brave.ClientResponseAdapter;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.hogdeer.extend.common.brave.utils.BraveUtils;
import com.hogdeer.extend.common.exception.BusinessException;
import com.hogdeer.extend.common.utils.ArrayHandleUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zipkin.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * <p>File：DubboClientResponseAdapter.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * @version 1.0
 */
public class DubboClientResponseAdapter implements ClientResponseAdapter {
	
	private final static Logger logger = LoggerFactory.getLogger(DubboClientResponseAdapter.class);

    private Result rpcResult ;

    private Exception exception;

    public DubboClientResponseAdapter(Exception exception) {
        this.exception = exception;
    }



    public DubboClientResponseAdapter(Result rpcResult) {
        this.rpcResult = rpcResult;
    }

    public Collection<KeyValueAnnotation> responseAnnotations() {
        List<KeyValueAnnotation> annotations = new ArrayList<KeyValueAnnotation>();
        try{
	        if(exception != null){
	        	this.setException(annotations, exception);
	        }else{
	            if(rpcResult.hasException()){  
	            	this.setException(annotations, (Throwable) rpcResult.getException()); 
	            }else{
	            	
	                KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("status","success");
	                annotations.add(keyValueAnnotation);
	                Object result = this.rpcResult.getValue();
	                if(null != result){
	                	List<Object> copyList = ArrayHandleUtils.copyList(result, null);
	                	if(CollectionUtils.isNotEmpty(copyList)){
	                		result = copyList;
	                	}
	                	/*long sizeOf = RamUsageEstimator.sizeOf(result);
	                	if(sizeOf < 30000L){
		                	KeyValueAnnotation resultAnnotation=  KeyValueAnnotation.create("rpc.result", JSON.toJSONString(result));
		                	annotations.add(resultAnnotation);
	                	}*/
	                	String jsonStr = JSON.toJSONString(result);
						if(jsonStr.length()<2000){
							KeyValueAnnotation resultAnnotation=  KeyValueAnnotation.create("rpc.result", JSON.toJSONString(result));
		                	annotations.add(resultAnnotation);
						}
	                }   
	            }
	        }
        }catch(Exception e){
        	logger.error(e.getMessage(), e);
        }
        return annotations;
    }
    
    private void setException(List<KeyValueAnnotation> annotations, Throwable exception){
    	KeyValueAnnotation errValueAnnotation=  KeyValueAnnotation.create(Constants.ERROR, "1");
    	annotations.add(errValueAnnotation);
        KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("error.msg", this.getErrorMsg(exception));
        annotations.add(keyValueAnnotation);
        BraveUtils.setExceptionTypeAnnotaion(annotations, exception);
    }
    
    public String getErrorMsg(Throwable throwable){
    	String errMsg = "";
    	if(throwable instanceof BusinessException){
    		BusinessException busException = (BusinessException) throwable;
    		errMsg = busException.getNativeMessage();
    	}else{
    		errMsg = ExceptionUtils.getStackTrace(throwable);
    	}
    	return errMsg;
    }

}
