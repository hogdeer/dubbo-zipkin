package com.hogdeer.extend.common.exception;

import com.hogdeer.extend.common.bean.ErrorCodeDescribable;
import com.hogdeer.extend.common.consts.ApplicationConst;

import java.io.Serializable;

/**
 * <p>File：BusinessException.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2015年10月8日 下午12:19:59</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public class BusinessException extends RuntimeException
{
    //
    private static final long    serialVersionUID = -3267019434607947850L;

    private Integer code;
    
    private Object object;
    
    private String nativeMessage;

    // 错误描述代码
    private ErrorCodeDescribable errorCode;
    
    public BusinessException(String message)
    {
        this(ApplicationConst.ERROR_CODE_FAILURE, message); 
    }

    public BusinessException(Integer code, String message) 
    {
        super(message);
        this.code = code;
        this.errorCode = new GeneralErrorCode(code, message);
    } 

    public BusinessException(Integer code, String message, Object object)
    {
        this(code, message);
        this.object = object;
    } 

    /**
     * 
     * @param codeDescribable
     */
    public BusinessException(ErrorCodeDescribable codeDescribable)
    {
        super(new StringBuilder("Error code: ")
                .append(codeDescribable.getCode()).append(", description: ")
                .append(codeDescribable.getMessage()).toString());
        this.errorCode = codeDescribable;
    }
    
    public ErrorCodeDescribable getErrorCode()
    {
        return this.errorCode;
    }
    
    public Integer getCode()
    {
    	return code;
    }
    
    public String getMessage()
    {
    	return super.getMessage();
    }

    public Object getObject()
    {
        return object;
    }

    public void setObject(Object object)
    {
        this.object = object;
    }
    
    public String getNativeMessage() {
		return nativeMessage;
	}

	public void setNativeMessage(String nativeMessage) {
		this.nativeMessage = nativeMessage;
	}

	private class GeneralErrorCode implements ErrorCodeDescribable, Serializable
    {
        //
        private static final long serialVersionUID = 7730163976874124704L;

        private Integer code;
        
        private String message;
        
        public GeneralErrorCode(Integer code, String message){
            this.code = code;
            this.message = message;
        }

        /* (non-Javadoc)
         * @see com.zttx.bean.ErrorCodeDescribable#getCode()
         */
        public Integer getCode()
        {
            return this.code;
        }
        
        @SuppressWarnings("unused")
        public void setCode(Integer code){
            this.code = code;
        }

        /* (non-Javadoc)
         * @see com.zttx.bean.ErrorCodeDescribable#getMessage()
         */
        public String getMessage()
        {
            return this.message;
        }

        @SuppressWarnings("unused")
        public void setMessage(String message){
            this.message = message;
        }
        
    }
}
