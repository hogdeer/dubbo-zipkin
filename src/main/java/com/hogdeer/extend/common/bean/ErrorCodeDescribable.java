package com.hogdeer.extend.common.bean;

/**
 * <p>File：ErrorCodeDescribable.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2015年10月8日 下午12:18:49</p>
 * <p>Company: cargogm.com</p>
 * @author 施建波
 * @version 1.0
 */
public interface ErrorCodeDescribable 
{
    /**
     * 获取异常代码
     * @return
     */
    Integer getCode();

    /**
     * 获取异常代码描述
     * @return
     */
    String getMessage();
}
